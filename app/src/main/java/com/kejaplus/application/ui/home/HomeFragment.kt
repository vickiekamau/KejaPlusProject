package com.kejaplus.application.ui.home

import android.R
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SearchView
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

class HomeFragment : Fragment(), Communicator, SearchView.OnQueryTextListener,
    androidx.appcompat.widget.SearchView.OnQueryTextListener {

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



        propertyRecyclerView = _binding!!.recyclerview
        propertyRecyclerView.layoutManager = GridLayoutManager(mContext,3)

        _binding?.shimmerFrameLayout?.startShimmerAnimation()

        val ifOnline = homeViewModel.netConnectivity(mContext)
       if(ifOnline) {
           homeViewModel.fetchData.observe(viewLifecycleOwner) {
               _binding?.shimmerFrameLayout?.stopShimmerAnimation()
               _binding?.shimmerFrameLayout?.visibility = View.GONE
               _binding?.recyclerview?.visibility = View.VISIBLE
               Log.d("ONLINE DATA ", it.toString())
               propertyRecyclerView.adapter =
                   PropertyAdapter(it as ArrayList<SaveProperty>, mContext, this@HomeFragment)
           }
       }else {
           homeViewModel.fetchOfflineData.observe(viewLifecycleOwner) {
               _binding?.shimmerFrameLayout?.stopShimmerAnimation()
               _binding?.shimmerFrameLayout?.visibility = View.GONE
               _binding?.recyclerview?.visibility = View.VISIBLE
               Log.d("OFFLINE DATA ", it.toString())
               propertyRecyclerView.adapter =
                   PropertyAdapter(it as ArrayList<SaveProperty>, mContext, this@HomeFragment)
           }
       }

        /**homeViewModel.propertyItems.observe(viewLifecycleOwner){

            _binding?.shimmerFrameLayout?.stopShimmerAnimation()
            _binding?.shimmerFrameLayout?.visibility = View.GONE
            _binding?.recyclerview?.visibility = View.VISIBLE
            propertyRecyclerView.adapter = PropertyAdapter(it as ArrayList<SaveProperty>,mContext,this@HomeFragment)
        }*/

        // initialize the search view
        _binding?.searchView?.isSubmitButtonEnabled = true
        _binding?.searchView?.setOnQueryTextListener(this)



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

    override fun onQueryTextSubmit(query: String?): Boolean {

        if (query != null) {
            homeViewModel.search(query.trim())
            Log.d("search",query.trim())
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {

        if (newText != null) {
            homeViewModel.search(newText.trim())
            Log.d("search",newText.trim())
        }
        return true
    }
}
