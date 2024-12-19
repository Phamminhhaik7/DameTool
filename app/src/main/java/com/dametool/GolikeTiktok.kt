package com.dametool

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dametool.databinding.GolikeTiktokBinding

class GolikeTiktok : Fragment() {

    private var _binding: GolikeTiktokBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GolikeTiktokBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.included.TraoDoiSubHome.setOnClickListener {
            toolinuse = TraoDoiSubHome()
            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, toolinuse)
                .commit()
        }
        binding.included.TuongTacCheoHome.setOnClickListener {
            toolinuse = TuongTacCheoHome()
            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, toolinuse)
                .commit()
        }
        binding.included.GolikeHome.setOnClickListener {
            toolinuse = GolikeHome()
            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, toolinuse)
                .commit()
        }
        binding.StartGolikeTiktok.setOnClickListener {
            startFloatingService(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun startFloatingService(context: Context) {
        val intent = Intent(context, GolikeTikTokFloating::class.java)
        context.startService(intent)
    }
}