package com.example.myapplication;

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

data class Announcement(
    val title: String,
    val message: String,
    val date: String,
    val priority: String
)

class AnnouncementActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AnnouncementAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_announcement)

        // Enable back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Announcements"

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.announcementRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Load sample announcements
        val announcements = getSampleAnnouncements()
        adapter = AnnouncementAdapter(announcements)
        recyclerView.adapter = adapter
    }

    private fun getSampleAnnouncements(): List<Announcement> {
        return listOf(
            Announcement(
                "Flood Warning Lifted",
                "The flood warning for Calauan area has been lifted. Water levels are returning to normal. Residents can resume regular activities.",
                "Oct 07, 2025",
                "Info"
            ),
            Announcement(
                "Heavy Rainfall Expected",
                "Weather bureau forecasts heavy rainfall in the next 48 hours. Please stay alert and monitor water levels in your area.",
                "Oct 06, 2025",
                "Warning"
            ),
            Announcement(
                "Evacuation Drill Scheduled",
                "A flood evacuation drill will be conducted on October 15, 2025. All residents are encouraged to participate.",
                "Oct 05, 2025",
                "Info"
            ),
            Announcement(
                "New Flood Monitoring Station",
                "A new flood monitoring station has been installed in Barangay area. Real-time data is now available on the dashboard.",
                "Oct 03, 2025",
                "Info"
            ),
            Announcement(
                "Emergency Hotlines",
                "Save these emergency numbers: Local DRRM Office: 123-4567, Red Cross: 143, Emergency Rescue: 911",
                "Oct 01, 2025",
                "Important"
            )
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

class AnnouncementAdapter(private val announcements: List<Announcement>) :
    RecyclerView.Adapter<AnnouncementAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.announcementTitle)
        val messageText: TextView = view.findViewById(R.id.announcementMessage)
        val dateText: TextView = view.findViewById(R.id.announcementDate)
        val priorityBadge: TextView = view.findViewById(R.id.priorityBadge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_announcement, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val announcement = announcements[position]
        holder.titleText.text = announcement.title
        holder.messageText.text = announcement.message
        holder.dateText.text = announcement.date
        holder.priorityBadge.text = announcement.priority

        // Set priority badge color
        val color = when (announcement.priority) {
            "Warning" -> android.graphics.Color.RED
            "Important" -> android.graphics.Color.parseColor("#FFA500")
            else -> android.graphics.Color.parseColor("#4CAF50")
        }
        holder.priorityBadge.setBackgroundColor(color)
    }

    override fun getItemCount() = announcements.size
}