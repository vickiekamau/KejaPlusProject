package com.kejaplus.application.ui.home

import android.R
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kejaplus.Model.SaveProperty
import com.google.firebase.database.*
import com.kejaplus.application.Adapters.PropertyAdapter
import com.kejaplus.application.databinding.FragmentHomeBinding
import com.kejaplus.application.interfaces.Communicator

class HomeFragment : Fragment(), Communicator {

    private val homeViewModel: HomeViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null
    private lateinit var mContext: Context
    private lateinit var databaseReference: DatabaseReference
    private lateinit var propertyRecyclerView: RecyclerView



    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        mContext = container!!.context

        //val homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]


        propertyRecyclerView = _binding!!.recyclerview
        propertyRecyclerView.layoutManager = GridLayoutManager(mContext,3)

        _binding?.shimmerFrameLayout?.startShimmerAnimation()

        //homeViewModel.getPropertyData()

        homeViewModel.allProperties.observe(viewLifecycleOwner){
            _binding?.shimmerFrameLayout?.stopShimmerAnimation()
            _binding?.shimmerFrameLayout?.visibility = View.GONE
            _binding?.recyclerview?.visibility = View.VISIBLE
            propertyRecyclerView.adapter = PropertyAdapter(it as ArrayList<SaveProperty>,mContext,this@HomeFragment)
        }

        /**homeViewModel.propertyItems.observe(viewLifecycleOwner){

            _binding?.shimmerFrameLayout?.stopShimmerAnimation()
            _binding?.shimmerFrameLayout?.visibility = View.GONE
            _binding?.recyclerview?.visibility = View.VISIBLE
            propertyRecyclerView.adapter = PropertyAdapter(it as ArrayList<SaveProperty>,mContext,this@HomeFragment)
        }*/



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        _binding?.shimmerFrameLayout?.startShimmerAnimation()
    }

    override fun onPause() {
        _binding?.shimmerFrameLayout?.stopShimmerAnimation()
        super.onPause()
    }

    override fun passData(
        position: Int,
        propertyName: String,
        propertyType: String,
        propertyDesc: String,
        propertyLocation: String,
        propertyCondition: String,
        propertyCategory:String,
        price: String,
        timeStamp: String,
        propertyImage: String
    ) {
        val action =  HomeFragmentDirections.actionNavigationHomeToPropertyDetailsFragment(propertyName,propertyType,propertyDesc,propertyCondition,propertyLocation,price,propertyCategory,timeStamp,propertyImage)
        findNavController().navigate(action)
    }
}
