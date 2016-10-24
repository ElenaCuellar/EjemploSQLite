# EjemploSQLite

This is a practise for my classes in which I use WebView and SQLite DB in Android Studio.

The app is a web browser which stores the urls you visit.
Thus, when you begin to write an url you visited earlier, the AutocompleteTextView shows you suggestions.

In short, it is composed by an activity_main.xml and 5 .java: 
MainActivity.java: contains references to Views and the WebView functionality plus some general functionality.
AdminSQL.java: contains the DB functionality.
MiAutoCompleteView.java: contains some specifications to the AutoCompleteView.
MiAutoCompleteTextChangedListener.java: contains the performance of the listener called by MiAutoCompleteView.
MiObjeto.java: an object to create instances of urls which will be added to the database.
