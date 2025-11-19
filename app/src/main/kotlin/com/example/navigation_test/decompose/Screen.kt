package com.example.navigation_test.decompose

sealed interface PrimaryScreen {
    data object Auth : PrimaryScreen
    data object Settings : PrimaryScreen

    data class TabScreen(val component: MainComponent) : PrimaryScreen
}

sealed interface SecondaryScreen {
    val name: String
    val position: Int

    interface WebSocketTab
    data object Tab1 : SecondaryScreen, WebSocketTab {
        override val position = 0
        override val name = "Tab 1"
    }

    data object Tab2 : SecondaryScreen, WebSocketTab {
        override val position = 1
        override val name = "Tab 2"
    }

    data object Tab3 : SecondaryScreen {
        override val position = 2
        override val name = "Tab 3"
    }
}
