package com.albertomier.githubapp.ui.user

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.albertomier.githubapp.AppExecutors
import com.albertomier.githubapp.R
import com.albertomier.githubapp.binding.FragmentDataBindingComponent
import com.albertomier.githubapp.databinding.FragmentUserBinding
import com.albertomier.githubapp.di.Injectable
import com.albertomier.githubapp.ui.common.RepoListAdapter
import com.albertomier.githubapp.ui.common.RetryCallback
import com.albertomier.githubapp.utils.autoCleared
import javax.inject.Inject

class UserFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    var binding by autoCleared<FragmentUserBinding>()

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private val userViewModel: UserViewModel by viewModels {
        viewModelFactory
    }

    private val params by navArgs<UserFragmentArgs>()
    private var adapter by autoCleared<RepoListAdapter>()
    private var handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dataBinding = DataBindingUtil.inflate<FragmentUserBinding>(
            inflater,
            R.layout.fragment_user,
            container,
            false,
            dataBindingComponent
        )

        dataBinding.retryCallback = object : RetryCallback {
            override fun retry() {
                userViewModel.retry()
            }
        }

        binding = dataBinding

//        sharedElementEnterTransition =
//            TransitionInflater.from(context).inflateTransition(R.transition.move)

        //postponeEnterTransition()
        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val params = UserFragmentArgs.fromBundle(requireArguments())

        userViewModel.setLogin(params.login)
        userViewModel.user.observe(this, Observer { userResource ->
            binding.user = userResource.data
            binding.userResource = userResource
        })

        val rvAdapter = RepoListAdapter(
            dataBindingComponent = dataBindingComponent,
            appExecutors = appExecutors,
            showFullName = false
        ) { repo ->
            findNavController().navigate(
                UserFragmentDirections.actionUserFragmentToRepoFragment(
                    repo.name,
                    repo.owner.login
                )
            )
        }

        binding.repoList.adapter = rvAdapter
        this.adapter = rvAdapter

        initRepoList()
    }

    private fun initRepoList() {
        userViewModel.repositories.observe(this, Observer { repos ->
            adapter.submitList(repos.data)
        })
    }
}