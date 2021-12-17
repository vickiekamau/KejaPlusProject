package com.kejaplus.application.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kejaplus.Model.SaveProperty
import com.google.firebase.database.*
import com.google.firebase.storage.StorageReference
import com.kejaplus.application.Adapters.ImageAdapter
import com.kejaplus.application.R
import com.kejaplus.application.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private lateinit var mContext: Context
    private lateinit var databaseReference: DatabaseReference
    private lateinit var propertyRecyclerView: RecyclerView
    private lateinit var propertyArrayList : ArrayList<SaveProperty>

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        mContext = container!!.context

        propertyRecyclerView = _binding!!.recyclerview
        propertyRecyclerView.layoutManager = GridLayoutManager(mContext,3)
        propertyArrayList = arrayListOf<SaveProperty>()
        _binding?.shimmerFrameLayout?.startShimmerAnimation()
        getPropertyData()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun getPropertyData() {
            databaseReference = FirebaseDatabase.getInstance().getReference("property")
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        for (propertySnapshot in snapshot.children){
                            val property = propertySnapshot.getValue(SaveProperty::class.java)
                            propertyArrayList.add(property!!)
                        }
                        _binding?.shimmerFrameLayout?.stopShimmerAnimation()
                        _binding?.shimmerFrameLayout?.visibility = View.GONE
                        _binding?.recyclerview?.visibility = View.VISIBLE
                        propertyRecyclerView.adapter = ImageAdapter(propertyArrayList,mContext)

                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    _binding?.shimmerFrameLayout?.visibility = View.GONE
                    Toast.makeText(requireActivity().application,error.toString(),Toast.LENGTH_LONG).show()
                }

            })

        }

    override fun onResume() {
        super.onResume()
        _binding?.shimmerFrameLayout?.startShimmerAnimation()
    }

    override fun onPause() {
        _binding?.shimmerFrameLayout?.stopShimmerAnimation()
        super.onPause()
    }
    }
