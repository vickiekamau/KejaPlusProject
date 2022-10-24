package com.kejaplus.application.ui.notification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.kejaplus.application.Adapters.NotificationAdapter
import com.kejaplus.application.R
import com.kejaplus.application.databinding.FragmentNotificationBinding
import com.kejaplus.application.ui.authentication.SignInActivity
import com.kejaplus.application.ui.home.HomeViewModel
import com.kejaplus.utils.SweetAlerts
import kotlinx.coroutines.flow.collectLatest


class NotificationFragment: Fragment() {

    private lateinit var binding: FragmentNotificationBinding
    private val viewModel: NotificationViewModel by viewModels()
    private val adapter = NotificationAdapter()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        setHasOptionsMenu(true)

        binding = FragmentNotificationBinding.inflate(inflater,container,false)

        binding.recyclerview.adapter = adapter



        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(true)
        (requireActivity() as AppCompatActivity?)!!.supportActionBar!!.show()


        lifecycleScope.launchWhenResumed {
            viewModel.notifications.collectLatest {
                adapter.submitData(it)
            }
        }


    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.apply {
            findItem(R.id.notification).isVisible = false
            findItem(R.id.logout).isVisible = false
            findItem(R.id.main_menu_mark_all_as_read).isVisible = true
            findItem(R.id.main_menu_clear_all_notification).isVisible = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.main_menu_clear_all_notification -> {
                showConfirmation(
                    context = requireContext(),
                    "Are You Sure?",
                    "Clear All Notifications?",
                    "No",
                    confirm = {
                        viewModel.clearAll()
                    })
            }
            R.id.main_menu_mark_all_as_read -> {
                showConfirmation(
                    context = requireContext(),
                    "Are You Sure?",
                    "Clear All Notifications?",
                    "No",
                    confirm = {
                        viewModel.markAllAsRead()
                    })

            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        viewModel.markAllAsRead()
    }



    private fun showConfirmation(context: Context, title: String, msg: String, cancelT:String, confirm: () -> Unit) {
        SweetAlerts.confirm(
            context = context,
            title = title,
            msg = msg,
            cancelText = cancelT,
            confirm = confirm
        )
    }

}