## FirebaseCrud

A simple task list app that provides create, read, update and delete operations using the Firebase RT database.

### App Highlights

It uses a `RecyclerView` to display a list of `EditText` widgets.
*Firebase Remote Config* is also demonstrated to load new task hints. An actual device is required to test/use *Remote Config*.

The `RecyclerView` logic shows how to use a *listener* to pass events to the Activity.
Each `ViewHolder` responds to *Done* and *Delete* actions to manipulate the task list.

### Firebase Highlights

- Handles all realtime syncing from clients to server.
- Provides a simple local database for offline use.
- Supports the most common offline scenarios. Then resync data once the client comes back online.

### Dev Notes

- You will need to provide your own `google-services.json` file. Which you get from your *Firebase Console*.
- You will need an actual device for *Firebase Remote Config* to work. It does not work in the Emulator.
- Update your database rules to allow `read` and `write` without *auth*.

### Helpful Links

- [Setup](https://firebase.google.com/docs/android/setup)
- [Get Started](https://firebase.google.com/docs/database/android/start/)
- [API Reference](https://firebase.google.com/docs/reference/android/com/google/firebase/database/package-summary)

### License

MIT
