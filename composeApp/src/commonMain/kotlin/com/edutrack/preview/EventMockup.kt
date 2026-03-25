package com.edutrack.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.edutrack.core.data.calendar.CalendarDay
import com.edutrack.core.data.calendar.CalendarEvent
import com.edutrack.core.presentation.screens.GenericCalendarScreen

private val mockEvents = listOf(
    CalendarEvent(
        id = "1",
        title = "Tech Meetup: Kotlin Multiplatform",
        startMillis = 1741100400000L, // Mar 4 2025 10:00 AM
        endMillis = 1741111200000L,   // Mar 4 2025 1:00 PM
        description = "Speaker: Jane Doe",
        location = "Innovation Hub, Room 3A",
        color = Color(0xFF4CAF50)
    ),
    CalendarEvent(
        id = "2",
        title = "Design Workshop",
        startMillis = 1741114800000L, // Mar 4 2025 2:00 PM
        endMillis = 1741122000000L,   // Mar 4 2025 4:00 PM
        description = "Hands-on Figma session",
        location = "Creative Studio B",
        color = Color(0xFF2196F3)
    ),
    CalendarEvent(
        id = "3",
        title = "Networking Happy Hour",
        startMillis = 1741126800000L, // Mar 4 2025 5:30 PM
        endMillis = 1741134000000L,   // Mar 4 2025 7:30 PM
        location = "Rooftop Lounge",
        color = Color(0xFFFF9800)
    )
)

private val agendaGroups = mapOf(
    "Today, Mar 4" to mockEvents,
    "Tomorrow, Mar 5" to listOf(
        CalendarEvent(
            id = "4",
            title = "Product Launch Keynote",
            startMillis = 1741186800000L,
            endMillis = 1741197600000L,
            location = "Main Auditorium",
            color = Color(0xFF9C27B0)
        ),
        CalendarEvent(
            id = "5",
            title = "Panel: Future of Mobile Dev",
            startMillis = 1741201200000L,
            endMillis = 1741208400000L,
            location = "Conference Hall A",
            color = Color(0xFF009688)
        )
    ),
    "Thu, Mar 6" to listOf(
        CalendarEvent(
            id = "6",
            title = "Closing Ceremony & Awards",
            startMillis = 1741276800000L,
            endMillis = 1741284000000L,
            location = "Grand Ballroom",
            color = Color(0xFFE91E63)
        )
    )
)

private fun buildMarchDays(): List<CalendarDay> {
    val days = mutableListOf<CalendarDay>()
    // Feb padding (Sat Mar 1 2025 → need 6 padding days for Sun-start grid)
    for (i in 1..6) {
        days.add(CalendarDay(dateMillis = 0L, dayOfMonth = 23 + i, isCurrentMonth = false, isToday = false, isSelected = false))
    }
    for (d in 1..31) {
        days.add(
            CalendarDay(
                dateMillis = 1740787200000L + (d - 1) * 86400000L,
                dayOfMonth = d,
                isCurrentMonth = true,
                isToday = d == 4,
                isSelected = d == 4,
                events = if (d == 4) mockEvents else if (d == 5) agendaGroups["Tomorrow, Mar 5"] ?: emptyList() else emptyList()
            )
        )
    }
    // April padding
    for (i in 1..5) {
        days.add(CalendarDay(dateMillis = 0L, dayOfMonth = i, isCurrentMonth = false, isToday = false, isSelected = false))
    }
    return days
}

@Composable
fun EventMockup() {
    GenericCalendarScreen(
        title = "TechConf 2025",
        monthLabel = "March 2025",
        days = buildMarchDays(),
        selectedDayEvents = mockEvents,
        onDayClick = {},
        onAddEvent = {},
        onEventClick = {},
        agendaGroups = agendaGroups
    )
}
