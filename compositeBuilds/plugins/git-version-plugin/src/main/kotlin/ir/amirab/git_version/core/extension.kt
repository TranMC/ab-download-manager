package ir.amirab.git_version.core

import org.eclipse.jgit.lib.RepositoryBuilder
import org.intellij.lang.annotations.Language
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.helpers.NOPLogger
import java.io.File
import kotlin.math.log

open class MatchedRef<T : GitReference>(
    val refInfo: T
)

class MatchedRefWithResult<T : GitReference>(
    refInfo: T,
    val matchResult: MatchResult,
) : MatchedRef<T>(refInfo)

class ResolvedScope() {
    private val tagFilter = linkedMapOf<String, (MatchedRefWithResult<GitReference.TagInfo>) -> String?>()
    private val branchFilter = linkedMapOf<String, (MatchedRefWithResult<GitReference.BranchInfo>) -> String?>()
    private var _commit: (MatchedRef<GitReference.ShaReference>) -> String? = { null }

    //should match entire tag name
    fun branch(
        @Language("RegExp") regex: String, function: (MatchedRefWithResult<GitReference.BranchInfo>) -> String?
    ) {
        branchFilter[regex] = function
    }

    //should match entire tag name
    fun tag(
        @Language("RegExp") regex: String,
        function: (MatchedRefWithResult<GitReference.TagInfo>) -> String?
    ) {
        tagFilter[regex] = function
    }

    fun commit(function: (MatchedRef<GitReference.ShaReference>) -> String?) {
        _commit = function
    }

    private fun matchTag(tagInfo: GitReference.TagInfo): String? {
        for ((regex, matchedRef) in tagFilter) {
            val matched = regex.toRegex().matchEntire(tagInfo.shortenName)
            if (matched != null) {
                matchedRef(MatchedRefWithResult(tagInfo, matched))?.let { result ->
                    return result
                } ?: continue
            }
        }
        return null
    }

    private fun matchBranch(branchInfo: GitReference.BranchInfo): String? {
        for ((regex, matchedRef) in branchFilter) {
            val matched = regex.toRegex().matchEntire(branchInfo.shortenName)
            if (matched != null) {
                matchedRef(MatchedRefWithResult(branchInfo, matched))?.let { result ->
                    return result
                } ?: continue
            }
        }
        return null
    }

    private fun matchCommit(commitInfo: GitReference.ShaReference): String? {
        return _commit(MatchedRef(commitInfo))
    }

    fun match(gitReference: GitReference): String? {
        return when (gitReference) {
            is GitReference.BranchInfo -> matchBranch(gitReference)
            is GitReference.TagInfo -> matchTag(gitReference)
            is GitReference.ShaReference -> matchCommit(gitReference)
        }
    }
}

class GitVersionExtension {
    var preferTag: Boolean = false
    var preferCi: Boolean = true
    var checkCi: Boolean = true
    var tagSelector: TagSelector = SelectBestSemanticVersion()
    val transform: (String) -> String = { it.toSlug() }

    var currentWorkingDirectory: File = File(".")
    private var _logger:Logger?=null
    fun setLogger(logger: Logger){
        _logger= logger
    }
    fun getLogger():Logger{
        if (_logger==null){
            _logger=object :NOPLogger(){}
        }
        return _logger!!
    }

    private val repository by lazy {
        RepositoryBuilder()
            .findGitDir(currentWorkingDirectory)
            .build()
    }
    private val refHandlers = ResolvedScope()
    fun on(block: ResolvedScope.() -> Unit) {
        refHandlers.apply(block)
    }


    operator fun invoke(block: GitVersionExtension.() -> Unit) = apply { block() }

    private fun tryGetVersionFromCi(): GitReference? {
        return CiReferenceProvider.getAll().firstOrNull {
            it.isAvailable()
        }?.getReference()
    }

    private fun tryGetVersionFromGit(
        status: GitStatus = GitStatus(repository)
    ): GitReference? {
        val getTag = {
            status.tags
                .also { getLogger().info("${it.count()} tags found. ${it.map { it.shortenName }.joinToString(" , ")}") }
                .let { tagSelector.select(it) }
        }
        val getBranch = {
            status.branch?.also {
                getLogger().info("branch ${it.shortenName} detected")
            }
        }

        return if (preferTag) {
            getTag() ?: getBranch()
        } else {
            getBranch() ?: getTag()
        }
    }

    fun getBestReference(): GitReference? {
        val ci = { if (checkCi) tryGetVersionFromCi() else null }
        val gitStatus by lazy {
            GitStatus(repository)
        }
        val git = { tryGetVersionFromGit(gitStatus) }
        return when {
            preferCi -> ci() ?: git()
            else -> git() ?: ci()
        } ?: GitReference.ShaReference(gitStatus.head.name)
    }

    fun getVersion() = getBestReference()
        ?.let(refHandlers::match)
        ?.let(transform)
}