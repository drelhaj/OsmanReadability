package org.project.osman.process;

import java.io.IOException;

public class TestOSMAN {
		//create an instance of the OsmanReadability classs
		static OsmanReadability osman = new OsmanReadability();
		
		/**
		 * Test class of the OSMAN Readability package for computing Readability Metrics for Arabic text.
		 *
		 * @author Mahmoud El-Haj
		 * dr.melhaj@gmail.com
		 * @version 1.0
		 */
		public static void main(String[] args) throws InterruptedException, IOException{

		//loading the Arabic dictionary - needed for tokenization and sentence splitting.
		osman.loadData();
		
		//text in unicode in case your editor does not support Arabic text
		//text in Arabic: "��� ��� ����� ��� �������. ��� ��� ����� ���������"  [set encoding UTF-8 to view this]
		//This is an example of an Easy to read Arabic text from a children book
		String easyText = "\u0630\u0647\u0628 \u0647\u0646\u062f \u0648\u0623\u062d\u0645\u062f \u0627\u0644\u0649 \u0627\u0644\u0645\u062f\u0631\u0633\u0629\u002e \u0647\u0646\u062f \u062a\u062d\u0628 \u0627\u0644\u0631\u0633\u0645 \u0648\u0627\u0644\u0645\u0637\u0627\u0644\u0639\u0629";
		easyText = osman.addTashkeel(easyText);
		System.out.println(easyText);
		double osmanScore = osman.calculateOsman(easyText);
		System.out.println("Osman Score: " + osmanScore);
		System.out.println("----------------------------------\n");
		
		//This is an example from Sports news paper paragraph
		String harderText = "\u0643\u0634\u0641\u062a \u062f\u0631\u0627\u0633\u0629 \u0623\u062c\u0631\u062a\u0647\u0627 \u0628\u064a \u0628\u064a \u0633\u064a \u0625\u0646 \u062a\u0643\u0644\u0641\u0629 \u0645\u0634\u0627\u0647\u062f\u0629 \u0645\u0628\u0627\u0631\u0627\u064a\u0627\u062a \u0643\u0631\u0629 \u0627\u0644\u0642\u062f\u0645 \u0641\u064a \u0628\u0631\u064a\u0637\u0627\u0646\u064a\u0627 \u0627\u0646\u062e\u0641\u0636\u062a \u0623\u0648 \u0638\u0644\u062a \u0639\u0644\u0649 \u0645\u0627 \u0647\u064a \u0639\u0644\u064a\u0647 \u0644\u063a\u0627\u0644\u0628\u064a\u0629 \u0645\u0634\u062c\u0639\u064a \u0627\u0644\u0644\u0639\u0628\u0629\u002e";
		String hardertextAddedTashkeel = osman.addTashkeel(harderText);
		System.out.println(hardertextAddedTashkeel);
		double osmanScore2 = osman.calculateOsman(hardertextAddedTashkeel);
		System.out.println("Osman Score: " + osmanScore2);	
		System.out.println("----------------------------------\n");

		
		//This is an example of a harder to read text from a University book
		String hardText = "\u0648\u0623\u0646 \u0645\u0636\u0627\u0645\u064a\u0646 \u0627\u0644\u0646\u0645\u0627\u0630\u062c \u0627\u0644\u0631\u064a\u0627\u0636\u064a\u0629 \u0641\u064a \u0623\u064a \u0639\u0644\u0645 \u0645\u0646 \u0627\u0644\u0639\u0644\u0648\u0645 \u0627\u0644\u0637\u0628\u064a\u0639\u064a\u0629 \u0644\u0627 \u064a\u062a\u062f\u062e\u0644 \u0641\u064a \u0634\u0623\u0646\u0647\u0627 \u0639\u0644\u0645 \u0627\u0644\u0631\u064a\u0627\u0636\u064a\u0627\u062a\u060c \u0641\u0627\u0644\u0645\u0639\u0627\u062f\u0644\u0629 \u0627\u0644\u0641\u064a\u0632\u064a\u0627\u0626\u064a\u0629 \u0627\u0644\u0631\u064a\u0627\u0636\u064a\u0629 \u0647\u064a \u0644\u063a\u0629 ";
		String hardtextAddedTashkeel = osman.addTashkeel(hardText);
		System.out.println(hardtextAddedTashkeel);
		double osmanScore1 = osman.calculateOsman(hardtextAddedTashkeel);
		System.out.println("Osman Score: " + osmanScore1);	
		System.out.println("----------------------------------\n");
		
		
		//text in unicode in case your editor does not support Arabic text
		//text in Arabic: "�������� ��������� ���������� ����� �������������"  [set encoding UTF-8 to view this]
		//This is just to show how the removeTashkeel function works
		String textWithTashkeel  = "\u0627\u0644\u0633\u0651\u0644\u0627\u0645\u064f \u0639\u064e\u0644\u064e\u064a\u0643\u064f\u0645\u0652 \u0648\u064e\u0631\u064e\u062d\u0652\u0645\u064e\u0629\u064f \u0627\u0644\u0644\u0647\u0650 \u0648\u064e\u0628\u064e\u0631\u064e\u0643\u064e\u0627\u062a\u064f\u0647\u064f";
		String textTashkeelRemoved = osman.removeTashkeel(textWithTashkeel);
		System.out.println("Text after removing diacritics (tashkeel):\n" +textTashkeelRemoved);

	}
}
