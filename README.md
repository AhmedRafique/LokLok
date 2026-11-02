# Lok Lok 🟢
> **High-Definition Voice & Video Calling and Secure Messaging Platform for Android**

Lok Lok is a full-featured communication and services mobile application modeled after **BOTIM**. It is engineered for resilient real-time audio/video communication over firewalled and NAT networks, telecom-grade native call integration, end-to-end encrypted messaging, media stories, and digital wallet services.

---

## 📱 Features

1. **Carrier-Grade VoIP Calling (WebRTC / LiveKit Core)**
   * 1-on-1 and Group Voice & Video Calls with adaptive bitrate and dynacast.
   * **Full-Screen Lockscreen UI:** Wakes device and shows incoming call screen with accept/decline buttons even when locked or phone is asleep.
   * **Android Telecom ConnectionService:** Integrates directly with Android's system call framework, supporting native in-call Bluetooth headsets and system audio routing.
   * **Background Calling Service:** Foreground service with microphone/camera permissions to prevent OS termination during active calls.

2. **Real-Time Instant Messaging & E2EE**
   * **End-to-End Encryption:** AES-256-GCM message encryption using hardware-backed Android KeyStore.
   * **Real-time WebSockets:** Live message delivery, read receipts (single/double checkmarks), and typing indicators.
   * **Offline-First:** Local persistence powered by Room Database with encrypted caching.
   * **Voice Notes & Media:** Integrated audio waveform player and media bubble attachments.

3. **Status & Stories (Ultra App Features)**
   * Share ephemeral status updates (Text, Photos, Video clips) with contact status rings.

4. **Lok Lok Pay (Fintech & Remittances)**
   * Digital wallet balance management.
   * Peer-to-peer (P2P) money transfers directly within the app.
   * Utility bill payments, mobile recharges, and international remittance foundation.

5. **Security & Linked Devices**
   * Phone number OTP verification.
   * Secure session tokens stored via `EncryptedSharedPreferences`.
   * QR code pairing for desktop and web sessions.

---

## 🏗️ Technical Architecture

Lok Lok follows **Clean Architecture** and **MVI / MVVM** with Jetpack Compose:

```
com.loklok.app/
├── core/
│   ├── crypto/           # AES-256-GCM AndroidKeyStore hardware encryption
│   ├── designsystem/     # Lok Lok Theme (Colors, Typography, Reusable Components)
│   ├── di/               # Hilt Dependency Injection modules
│   ├── navigation/       # Type-safe Jetpack Navigation Graph
│   ├── network/          # WebSocket client, Retrofit API interfaces, OkHttp
│   ├── security/         # EncryptedSharedPreferences session management
│   ├── telecom/          # LokLokConnectionService & CallForegroundService
│   └── webrtc/           # LiveKit SFU Client & Camera/Mic track manager
├── data/
│   ├── local/            # Room Database, Entities (Chat, CallHistory, Contact), DAOs
│   └── repository/       # Implementations of Auth, Chat, Call, and Contact repositories
├── domain/
│   ├── model/            # Domain models (User, ChatMessage, CallSession, Story, Wallet)
│   └── repository/       # Repository contracts
├── feature/
│   ├── auth/             # Welcome, Phone Auth, OTP Verification, Profile Setup
│   ├── calls/            # Full-screen CallActivity, ActiveCallScreen, Call History
│   ├── chats/            # Conversations list, 1:1 & Group Chat screen, Bubbles
│   ├── contacts/         # Contact discovery, Invite, Quick call/chat picker
│   ├── main/             # Bottom Navigation Container (5 tabs)
│   ├── settings/         # Profile, Linked Devices, Privacy, Notifications
│   ├── stories/          # Stories/Status feed, Status viewer
│   └── wallet/           # Lok Lok Pay balance, P2P money transfer, Services grid
└── MainActivity.kt       # Application entry point with runtime permission requests
```

---

## 🚀 Getting Started

### Prerequisites
* **Android Studio Iguana | 2023.2.1** or newer
* **JDK 17**
* **Android SDK:** `compileSdk 34`, `minSdk 26` (Android 8.0 Oreo+)

### Running the Android App
1. Open Android Studio and choose **Open an Existing Project**, selecting `e:\LokLok`.
2. Let Gradle sync project dependencies automatically.
3. Connect an Android device or start an emulator with Google APIs.
4. Click **Run 'app'** (`Shift + F10`).

---

## 🌐 Self-Hosted VoIP & Signaling Infrastructure

For production-grade WebRTC traversal across symmetric NAT networks (such as in UAE/MENA), a self-hosted SFU and TURN server is provided in `backend/`:

```bash
cd backend
docker-compose up -d
```

This spins up:
* **LiveKit Server (Port 7880/7881):** High-throughput WebRTC SFU.
* **Coturn Server (Port 3478/5349):** High-performance STUN/TURN relay.
* **Redis (Port 6379):** Distributed room state.
* **PostgreSQL (Port 5432):** Relational database for accounts & payments.
