package me.profiluefter.profinote.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import me.profiluefter.profinote.R
import me.profiluefter.profinote.databinding.FragmentLoginBinding
import me.profiluefter.profinote.models.LoginViewModel

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private val loginViewModel: LoginViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = DataBindingUtil.inflate<FragmentLoginBinding>(
        inflater,
        R.layout.fragment_login,
        container,
        false
    ).apply {
        lifecycleOwner = this@LoginFragment
        layoutViewModel = loginViewModel

        loginRegisterRedirect.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.register())
        }

        loginViewModel.state.observe(viewLifecycleOwner) {
            if(it == LoginViewModel.LoginState.SUCCESS)
                findNavController().navigate(LoginFragmentDirections.loginFinished())
        }
    }.root
}