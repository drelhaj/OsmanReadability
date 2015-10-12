# OsmanReadability

<h3>About</h3>
Open Source tool for Arabic text readability

The tool is a Java open source to calculate readability for Arabic text with and without diacritics. 
The tool works better with diacritics added in.

<h3>How to run</h3>
Class TestOSMAN shows how to measure OSMAN readability for text with and without diacritics.
Method calculateOsman(String text) can be called using an instance from the class<b>OsmanReadability</b>.
users can also add and remove diacritics using <b>addTashkeel(String text)</b> and <b>removeTashkeel(String text)</b>.

The tool allows you to calculate other readability metrics such as ARI and LIX.
When using Eclipse (or other editors) make sure you set the encoding to UTF-8 for the console output (Run configuration -> Common Tab --> Other Encoding)
