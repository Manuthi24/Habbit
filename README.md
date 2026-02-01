# Habbit - Personal Wellness Companion
![Kotlin](https://img.shields.io/badge/Kotlin-1.8%2B-blueviolet)
![Android](https://img.shields.io/badge/Android-Android%20Studio-green)
![Material Design](https://img.shields.io/badge/Material%20Design-UI%20UX-orange)
![MPAndroidChart](https://img.shields.io/badge/MPAndroidChart-Data%20Visualization-red)
![WorkManager](https://img.shields.io/badge/WorkManager-Background%20Tasks-lightgrey)
![SharedPreferences](https://img.shields.io/badge/SharedPreferences-Data%20Persistence-yellowgreen)
![Mobile App](https://img.shields.io/badge/Mobile%20App-Wellness%20Tracker-brightgreen)
![Portfolio Project](https://img.shields.io/badge/Type-Portfolio%20Project-important)


Habbit is a comprehensive Android application designed to help users manage their daily health routines and track their wellness journey. Built with Kotlin and Android Studio, it combines multiple features to promote personal wellness.

## 🌟 Features

### 1. Daily Habit Tracker
- **Add, Edit, Delete Habits**: Create custom wellness habits with names, descriptions, and frequency settings
- **Progress Tracking**: Visual progress indicators showing daily completion status
- **Smart Completion**: Mark habits as completed with visual feedback
- **Flexible Frequency**: Support for daily and weekly habits

### 2. Mood Journal with Emoji Selector
- **Emoji-Based Mood Logging**: Express emotions using a wide range of emojis
- **Note Taking**: Add personal notes to mood entries
- **Mood History**: View past mood entries with timestamps
- **Mood Trend Chart**: Visualize mood patterns over the past week using MPAndroidChart
- **Share Functionality**: Share mood entries with friends and family

### 3. Hydration Reminder System
- **Smart Notifications**: Configurable hydration reminders using WorkManager
- **Water Intake Tracking**: Track daily water consumption with visual progress
- **Customizable Goals**: Set personal daily water intake goals
- **Motivational Feedback**: Encouraging messages based on progress
- **Persistent Reminders**: Notifications survive device reboots

### 4. Advanced Features
- **Home Screen Widget**: Display today's habit completion percentage and water intake
- **Sensor Integration**: Automatic step counting using accelerometer
- **Shake Detection**: Quick mood entry via device shake gesture
- **Data Export**: Export all app data for backup or analysis

### 5. User Experience
- **Onboarding Screens**: Welcome new users with guided introduction
- **Responsive Design**: Optimized for phones, tablets, portrait, and landscape orientations
- **Modern UI**: Clean, intuitive interface with Material Design principles
- **Dark/Light Theme**: Theme customization options

## 🏗️ Technical Architecture

### Architecture Components
- **Fragments**: Modular UI components for different app sections
- **Navigation Component**: Seamless navigation between app sections
- **ViewPager2**: Smooth onboarding experience
- **RecyclerView**: Efficient list management for habits and mood entries
- **SharedPreferences**: Local data persistence without database complexity

### Data Management
- **DataManager Class**: Centralized data handling using SharedPreferences
- **Model Classes**: Well-structured data models with Parcelable support
- **Date Utilities**: Consistent date formatting and manipulation

### Background Services
- **WorkManager**: Reliable background task scheduling for notifications
- **Sensor Services**: Accelerometer-based step counting and shake detection
- **Notification Channels**: Organized notification management

### UI/UX Features
- **Material Design**: Modern Android design language implementation
- **Responsive Layouts**: Adaptive layouts for different screen sizes
- **Custom Drawables**: Vector icons and backgrounds
- **Color Theming**: Comprehensive color system for consistent branding

## 📱 Screenshots

The app includes:
- Welcome onboarding screens
- Habit management interface
- Mood logging with emoji selection
- Hydration tracking with progress visualization
- Settings and customization options
- Home screen widget

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24+ (Android 7.0)
- Kotlin 1.8+

### Installation
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Run on device or emulator

### Permissions Required
- `POST_NOTIFICATIONS`: For hydration reminders
- `WAKE_LOCK`: For background notifications
- `RECEIVE_BOOT_COMPLETED`: For persistent reminders
- `VIBRATE`: For notification feedback
- `ACTIVITY_RECOGNITION`: For step counting

## 🎯 Key Features Implementation

### Habit Tracker
- RecyclerView with custom adapter
- CRUD operations with SharedPreferences
- Progress visualization with ProgressBar
- Empty state handling

### Mood Journal
- Emoji grid selection interface
- Mood trend visualization with MPAndroidChart
- Share functionality using implicit intents
- Color-coded mood entries

### Hydration System
- WorkManager for reliable notifications
- Configurable reminder intervals
- Visual progress tracking
- Motivational feedback system

### Advanced Features
- App widget with RemoteViews
- Accelerometer-based step counting
- Shake gesture detection
- Data export functionality

## 📊 Data Persistence

All data is stored locally using SharedPreferences:
- **Habits**: Custom habit definitions and settings
- **Habit Completions**: Daily completion records
- **Mood Entries**: Timestamped mood logs with notes
- **Hydration Records**: Daily water intake tracking
- **App Settings**: User preferences and configurations

## 🎨 Design System

### Color Palette
- **Primary**: Indigo (#6366F1)
- **Secondary**: Emerald (#10B981)
- **Accent**: Amber (#F59E0B)
- **Mood Colors**: Specific colors for different mood states
- **Status Colors**: Success, warning, error indicators

### Typography
- **Headings**: Bold, 24sp
- **Body**: Regular, 16sp
- **Captions**: Regular, 14sp
- **Labels**: Regular, 12sp

## 🔧 Customization

### Settings Available
- Notification preferences
- Theme selection (Light/Dark/System)
- Hydration reminder intervals
- Daily water intake goals
- Data management options

### Widget Configuration
- Habit completion progress
- Water intake tracking
- Date display
- Click-to-open app functionality

## 📈 Performance Optimizations

- Efficient RecyclerView implementations
- Lazy loading of data
- Optimized sensor usage
- Background task optimization
- Memory-conscious image handling

## 🧪 Testing Considerations

The app is designed with testability in mind:
- Modular architecture
- Dependency injection ready
- Clear separation of concerns
- Comprehensive error handling

## 🚀 Future Enhancements

Potential improvements:
- Cloud synchronization
- Social features
- Advanced analytics
- Wear OS integration
- Voice input support

## 📄 License

This project is created for educational purposes as part of an Android development assignment.

## 👨‍💻 Development

Built with:
- **Kotlin**: Modern Android development language
- **Android Studio**: Official IDE
- **Material Design**: Google's design system
- **Navigation Component**: Modern navigation framework
- **WorkManager**: Background task management
- **MPAndroidChart**: Chart visualization library

---

**Habbit** - Your personal wellness companion for building healthy habits and tracking your mood. Start your wellness journey today! 🌟
