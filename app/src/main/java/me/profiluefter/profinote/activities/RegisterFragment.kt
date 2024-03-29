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
import me.profiluefter.profinote.databinding.FragmentRegisterBinding
import me.profiluefter.profinote.models.LoginViewModel

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private val loginViewModel: LoginViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = DataBindingUtil.inflate<FragmentRegisterBinding>(
        inflater,
        R.layout.fragment_register,
        container,
        false
    ).apply {
        lifecycleOwner = this@RegisterFragment
        layoutViewModel = loginViewModel

        loginViewModel.state.observe(viewLifecycleOwner) {
            if (it == LoginViewModel.LoginState.SUCCESS) {
                Snackbar.make(root, R.string.register_successful, Snackbar.LENGTH_LONG).show()
                findNavController().navigate(RegisterFragmentDirections.registrationFinished())
            }
        }

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false).apply {
            duration = resources.getInteger(R.integer.notian_animation_time).toLong()
        }
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
            duration = resources.getInteger(R.integer.notian_animation_time).toLong()
        }
    }.root
}