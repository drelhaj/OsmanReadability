package org.project.osman.process;

import java.io.IOException;

public class TestOSMAN {
		//create an instance of the OsmanReadability classs
		static OsmanReadability osman = new OsmanReadability();
		
	public static void main(String[] args) throws InterruptedException, IOException{
		

		
		//loading the Arabic dictionary - needed for tokenization and sentence splitting.
		osman.loadData();
		
		//text in unicode in case your editor does not support Arabic text
		//text in Arabic: "«·”¯·«„ı ⁄Û·ÛÌﬂı„˙ ÊÛ—ÛÕ˙„Û…ı «··Âˆ ÊÛ»Û—ÛﬂÛ« ıÂı"  [set encoding UTF-8 to view this]
		String easyText = "\u0627\u0644\u0633\u0651\u0644\u0627\u0645\u064f \u0639\u064e\u0644\u064e\u064a\u0643\u064f\u0645\u0652 \u0648\u064e\u0631\u064e\u062d\u0652\u0645\u064e\u0629\u064f \u0627\u0644\u0644\u0647\u0650 \u0648\u064e\u0628\u064e\u0631\u064e\u0643\u064e\u0627\u062a\u064f\u0647\u064f";
		System.out.println(easyText);
		double osmanScore = osman.calculateOsman(easyText);
		System.out.println("Osman Score: " + osmanScore);
		System.out.println("----------------------------------\n");

		
		String hardText = "\u0648\u0623\u0646 \u0645\u0636\u0627\u0645\u064a\u0646 \u0627\u0644\u0646\u0645\u0627\u0630\u062c \u0627\u0644\u0631\u064a\u0627\u0636\u064a\u0629 \u0641\u064a \u0623\u064a \u0639\u0644\u0645 \u0645\u0646 \u0627\u0644\u0639\u0644\u0648\u0645 \u0627\u0644\u0637\u0628\u064a\u0639\u064a\u0629 \u0644\u0627 \u064a\u062a\u062f\u062e\u0644 \u0641\u064a \u0634\u0623\u0646\u0647\u0627 \u0639\u0644\u0645 \u0627\u0644\u0631\u064a\u0627\u0636\u064a\u0627\u062a\u060c \u0641\u0627\u0644\u0645\u0639\u0627\u062f\u0644\u0629 \u0627\u0644\u0641\u064a\u0632\u064a\u0627\u0626\u064a\u0629 \u0627\u0644\u0631\u064a\u0627\u0636\u064a\u0629 \u0647\u064a \u0644\u063a\u0629 \u0627\u0644\u0641\u064a\u0632\u064a\u0627\u0621\u002e \u0641\u0627\u0644\u0641\u064a\u0632\u064a\u0627\u0621 \u0639\u0644\u0645 \u0645\u0633\u062a\u0642\u0644 \u0628\u0630\u0627\u062a\u0647 \u0648\u0644\u0647 \u0639\u062f\u0629 \u0641\u0631\u0648\u0639 \u0645\u062b\u0644 \u0627\u0644\u0641\u064a\u0632\u064a\u0627\u0621 \u0627\u0644\u0630\u0631\u064a\u0629 \u060c \u0627\u0644\u0641\u064a\u0632\u064a\u0627\u0621 \u0627\u0644\u0646\u0648\u0648\u064a\u0629\u060c \u0627\u0644\u0646\u0638\u0631\u064a\u0629 \u0627\u0644\u0646\u0633\u0628\u064a\u0629 \u060c \u0627\u0644\u0628\u0635\u0631\u064a\u0627\u062a\u060c \u0627\u0644\u0635\u0648\u062a\u064a\u0627\u062a\u060c \u0627\u0644\u0643\u0647\u0631\u0628\u064a\u0629\u060c \u0627\u0644\u0645\u063a\u0646\u0627\u0637\u064a\u0633\u064a\u0629 \u060c \u0627\u0644\u062f\u064a\u0646\u0627\u0645\u064a\u0643\u0627 \u0627\u0644\u062d\u0631\u0627\u0631\u064a\u0629";
		String hardtextAddedTashkeel = osman.addTashkeel(hardText);
		System.out.println(hardtextAddedTashkeel);
		double osmanScore1 = osman.calculateOsman(hardtextAddedTashkeel);
		System.out.println("Osman Score: " + osmanScore1);	
		System.out.println("----------------------------------\n");
		
		//text in unicode in case your editor does not support Arabic text
		//text in Arabic: "«·”¯·«„ı ⁄Û·ÛÌﬂı„˙ ÊÛ—ÛÕ˙„Û…ı «··Âˆ ÊÛ»Û—ÛﬂÛ« ıÂı"  [set encoding UTF-8 to view this]
		String textWithTashkeel  = "\u0627\u0644\u0633\u0651\u0644\u0627\u0645\u064f \u0639\u064e\u0644\u064e\u064a\u0643\u064f\u0645\u0652 \u0648\u064e\u0631\u064e\u062d\u0652\u0645\u064e\u0629\u064f \u0627\u0644\u0644\u0647\u0650 \u0648\u064e\u0628\u064e\u0631\u064e\u0643\u064e\u0627\u062a\u064f\u0647\u064f";
		String textTashkeelRemoved = osman.removeTashkeel(textWithTashkeel);
		System.out.println("Text after removing diacritics (tashkeel):\n" +textTashkeelRemoved);
		
	}
}
