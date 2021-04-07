package me.profiluefter.profinote.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels
import me.profiluefter.profinote.R
import me.profiluefter.profinote.databinding.FragmentRegisterBinding
import me.profiluefter.profinote.models.LoginViewModel

class RegisterFragment : Fragment() {
    private val loginViewModel: LoginViewModel by navGraphViewModels(R.id.nav_graph)

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
        layoutViewModel = loginViewModel
    }.root
}