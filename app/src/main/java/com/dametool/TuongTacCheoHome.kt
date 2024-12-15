package com.dametool

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dametool.databinding.TuongtaccheoHomeBinding

class TuongTacCheoHome : Fragment() {

    private var _binding: TuongtaccheoHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TuongtaccheoHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.TraoDoiSubHome.setOnClickListener {
            toolinuse = TraoDoiSubHome()
            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, toolinuse)
                .commit()
        }
        binding.GolikeHome.setOnClickListener {
            toolinuse = GolikeHome()
            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, toolinuse)
                .commit()
        }



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}