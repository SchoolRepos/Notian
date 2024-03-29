package me.profiluefter.profinote.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
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
            exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, false).apply {
                duration = resources.getInteger(R.integer.notian_animation_time).toLong()
            }
            reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
                duration = resources.getInteger(R.integer.notian_animation_time).toLong()
            }
            findNavController().navigate(LoginFragmentDirections.register())
        }

        loginViewModel.state.observe(viewLifecycleOwner) {
            if(it == LoginViewModel.LoginState.SUCCESS) {
                Snackbar.make(root, R.string.login_successful, Snackbar.LENGTH_LONG).show()
                findNavController().navigate(LoginFragmentDirections.loginFinished())
            }
        }
    }.root
}