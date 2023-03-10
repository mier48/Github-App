package com.albertomier.githubapp.ui.search

import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.albertomier.githubapp.model.Repo
import com.albertomier.githubapp.repository.RepoRepository
import com.albertomier.githubapp.repository.Resource
import com.albertomier.githubapp.repository.Status
import com.albertomier.githubapp.utils.AbsentLiveData
import java.util.*
import javax.inject.Inject

class SearchViewModel @Inject constructor(repoRepository: RepoRepository) : ViewModel() {

    private val query = MutableLiveData<String>()
    private val nextPageHandler = NextPageHandler(repoRepository)
    val queryLD: LiveData<String> = query

    val result: LiveData<Resource<List<Repo>>> = Transformations.switchMap(query) { search ->
        if (search.isNullOrBlank()) {
            AbsentLiveData.create()
        } else {
            repoRepository.search(search)
        }
    }
    val loadMoreStatus: LiveData<LoadMoreState> get() = nextPageHandler.loadMoreState

    fun setQuery(orignialInput: String) {
        val input = orignialInput.lowercase(Locale.getDefault()).trim()

        if (input == query.value) {
            return
        }

        nextPageHandler.reset()
        query.value = input
    }

    fun loadNextPage() {
        query.value?.let {
            if (it.isNotBlank()) {
                nextPageHandler.queryNextPage(it)
            }
        }
    }

    fun refresh() {
        query.value?.let {
            query.value = it
        }
    }

    class LoadMoreState(val isRunning: Boolean, val errorMessage: String?) {
        private var handleError = false

        val errorMessageIfNotHandled: String?
            get() {
                if (handleError) {
                    return null
                }

                handleError = true
                return errorMessage
            }
    }

    class NextPageHandler(private val repository: RepoRepository) : Observer<Resource<Boolean>> {
        private var nextPageLiveData: LiveData<Resource<Boolean>>? = null
        val loadMoreState = MutableLiveData<LoadMoreState>()
        private var query: String? = null
        private var _hasMore: Boolean = false
        val hasMore get() = _hasMore

        init {
            reset()
        }

        fun queryNextPage(query: String) {
            if (this.query == query) {
                return
            }
            unregister()
            this.query = query
            nextPageLiveData = repository.searchNextPage(query)
            loadMoreState.value = LoadMoreState(isRunning = true, errorMessage = null)
            nextPageLiveData?.observeForever(this)
        }

        override fun onChanged(result: Resource<Boolean>?) {
            if (result == null) {
                reset()
            } else {
                when (result.status) {
                    Status.SUCCESS -> {
                        _hasMore = result.data == true
                        unregister()
                        loadMoreState.setValue(
                            LoadMoreState(
                                isRunning = false,
                                errorMessage = null
                            )
                        )
                    }

                    Status.ERROR -> {
                        _hasMore = true
                        unregister()
                        loadMoreState.setValue(
                            LoadMoreState(
                                isRunning = false,
                                errorMessage = result.message
                            )
                        )
                    }

                    Status.LOADING -> {

                    }
                }
            }
        }

        private fun unregister() {
            nextPageLiveData?.removeObserver(this)
            nextPageLiveData = null

            if (_hasMore) {
                query = null
            }
        }

        fun reset() {
            unregister()
            _hasMore = true
            loadMoreState.value = LoadMoreState(isRunning = false, errorMessage = null)
        }
    }
}