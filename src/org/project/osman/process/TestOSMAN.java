package org.project.osman.process;

import java.io.IOException;

public class TestOSMAN {

	public static void main(String[] args) throws InterruptedException, IOException{
		
		//create an instance of the OsmanReadability classs
		OsmanReadability osman = new OsmanReadability();
		
		//loading the Arabic dictionary - needed for tokenization and sentence splitting.
		osman.loadData();
		
		//text in unicode in case your editor does not support Arabic text
		//text in Arabic: "ÇáÓøáÇãõ Úóáóíßõãú æóÑóÍúãóÉõ Çááåö æóÈóÑóßóÇÊõåõ"  [set encoding UTF-8 to view this]
		String text = "\u0627\u0644\u0633\u0651\u0644\u0627\u0645\u064f \u0639\u064e\u0644\u064e\u064a\u0643\u064f\u0645\u0652 \u0648\u064e\u0631\u064e\u062d\u0652\u0645\u064e\u0629\u064f \u0627\u0644\u0644\u0647\u0650 \u0648\u064e\u0628\u064e\u0631\u064e\u0643\u064e\u0627\u062a\u064f\u0647\u064f";
		System.out.println(text);
		double osmanScore = osman.calculateOsman(text);
		System.out.println("Osman Score: " + osmanScore);
		
		
		String textNoTashkeel = "\u0627\u0644\u0633\u0644\u0627\u0645 \u0639\u0644\u064a\u0643\u0645 \u0648\u0631\u062d\u0645\u0629 \u0627\u0644\u0644\u0647 \u0648\u0628\u0631\u0643\u0627\u062a\u0647";

		String textAddedTashkeel = osman.addTashkeel(textNoTashkeel);
		//String textAddedTashkeel = osman.addTashkeel(textNoTashkeel);
		System.out.println(textAddedTashkeel);
		double osmanScore2 = osman.calculateOsman(textAddedTashkeel);
		System.out.println("Osman Score: " + osmanScore2);
		
		//text in unicode in case your editor does not support Arabic text
		//text in Arabic: "ÇáÓøáÇãõ Úóáóíßõãú æóÑóÍúãóÉõ Çááåö æóÈóÑóßóÇÊõåõ"  [set encoding UTF-8 to view this]
		String textWithTashkeel  = "\u0627\u0644\u0633\u0651\u0644\u0627\u0645\u064f \u0639\u064e\u0644\u064e\u064a\u0643\u064f\u0645\u0652 \u0648\u064e\u0631\u064e\u062d\u0652\u0645\u064e\u0629\u064f \u0627\u0644\u0644\u0647\u0650 \u0648\u064e\u0628\u064e\u0631\u064e\u0643\u064e\u0627\u062a\u064f\u0647\u064f";
		String textTashkeelRemoved = osman.removeTashkeel(textWithTashkeel);
		System.out.println(textTashkeelRemoved);
		
		
	}
}
