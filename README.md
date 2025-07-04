# Osman Readability Metric

## 📢 Now Available in `textstat`

The **Osman Readability Metric** for Arabic text is now integrated into the [`textstat`](https://pypi.org/project/textstat/) Python package.

### 📦 Installation

```bash
pip install textstat
```

### ▶️ Example Usage

```python
import textstat

text = "أدخل النص العربي هنا"
score = textstat.osman(text)
print(score)
```

This returns the Osman readability score for your Arabic input text.

---

## 🧠 What is Osman?

**Osman** is a readability metric specifically designed for Arabic, inspired by traditional metrics like Flesch and Fog, but enriched with a unique component called **Faseeh**—a linguistic factor that accounts for Arabic fluency and complexity.

Originally developed in Java, the Osman tool supports texts with or without diacritics (Tashkeel). When Tashkeel is absent, the tool estimates syllables automatically for readability scoring.

📄 **Reference**:  
El-Haj, M., and Rayson. "OSMAN - A Novel Arabic Readability Metric".  
*LREC 2016, Portoroz, Slovenia.*  
[Paper PDF](https://aclanthology.org/L16-1038.pdf) | [Lancaster Link](http://www.lancaster.ac.uk/staff/elhaj/docs/elhajlrec2016Arabic.pdf)

---

## 🚀 What's New in 2020?

- Major code update on **14 August 2020**
- No longer depends on **Farasa** due to licensing
- Fully open-source and free
- Switched to lightweight **RegEx-based** tokenisation (instead of Stanford)

These changes reduce storage requirements and improve runtime speed.

---

## 🖥️ How to Run the Java Tool

You can use the graphical tool via the pre-built JAR file.

### ▶️ Run with GUI:

1. Download `Osman2020.jar` from the releases.
2. In your terminal or command prompt, run:

```bash
java -Dfile.encoding=UTF-8 -jar Osman2020.jar
```

💡 If you encounter Arabic display issues on Windows, you can:
- Change your system locale to Arabic (see [YouTube Tutorial](https://www.youtube.com/watch?v=XkczYaBlbNY))
- Run this first: `set JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8`

---

## 🔧 Java Source Code

Developers can clone the repo and use the classes directly:

- Use `OsmanReadability.calculateOsman(String text)`
- Strip diacritics using `removeTashkeel(String text)`
- Example usage: See `TestOSMAN.java`

The tool also calculates other readability scores such as **ARI** and **LIX**.  
📌 Ensure UTF-8 encoding is set for console output in your IDE.

---

## 🧰 Import into Eclipse (Beginner Friendly)

1. Install EGit via **Help → Install New Software**
2. Add the Juno update site, search for `git`, install EGit + Mylyn GitHub connector
3. Go to **File → Import → Git → Projects from GitHub**
4. Search `OsmanReadability` and select `drelhaj/OsmanReadability`
5. Finish setup and explore the project

---

## 📚 OSMAN UN Corpus

- Find data under the `Osman_UN_Corpus` directory
- To download: click on a zip file → **View Raw** to start download
- Or use the **Download ZIP** button on the GitHub repo

---

## 📬 Contact

For questions or feedback, email:  
**📧 dr.melhaj@gmail.com**
