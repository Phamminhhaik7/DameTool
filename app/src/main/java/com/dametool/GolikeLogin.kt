package com.dametool

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dametool.databinding.GolikeLoginBinding

class GolikeLogin : Fragment() {

    private var _binding : GolikeLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GolikeLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolinuse = GolikeLogin()
        binding.included.TraoDoiSubHome.setOnClickListener {
            toolinuse = TraoDoiSubHome()
            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, toolinuse)
                .addToBackStack(null)
                .commit()
        }
        binding.included.TuongTacCheoHome.setOnClickListener {
            toolinuse = TuongTacCheoHome()
            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, toolinuse)
                .addToBackStack(null)
                .commit()
        }
        binding.GolikeLogin.setOnClickListener {
            val authorization = binding.GolikeAuthorization.editText?.text.toString()
            if (authorization.isEmpty()) {
                return@setOnClickListener
            }


        }


    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}