	/*
	 * To change this license header, choose License Headers in Project Properties.
	 * To change this template file, choose Tools | Templates
	 * and open the template in the editor.
	 */
	package com.qcri.farasa.diacritize;

	import com.qcri.farasa.segmenter.ArabicUtils;
	import static com.qcri.farasa.segmenter.ArabicUtils.openFileForReading;
	import static com.qcri.farasa.segmenter.ArabicUtils.openFileForWriting;
	import java.io.BufferedReader;
	import java.io.BufferedWriter;
	import java.io.FileNotFoundException;
	import java.io.IOException;
	import com.qcri.farasa.pos.FarasaPOSTagger;
	import com.qcri.farasa.segmenter.Farasa;

	/**
	 *
	 * @author kareemdarwish
	 */
	public class FarasaDiacritizer {
	    private static String binDir = "";
	    public static Farasa farasaSegmenter = null;
	    public static FarasaPOSTagger farasaPOSTagger = null;
	    public static DiacritizeText dt;
	    
	    
	    public static void loadFarasa() throws FileNotFoundException, ClassNotFoundException, IOException, InterruptedException{
	        binDir = "FarasaData"; // directory containing all data and executables
	        if (!binDir.endsWith("/"))
	            binDir = binDir + "/";
	        farasaSegmenter = new Farasa();
	        farasaPOSTagger = new FarasaPOSTagger(farasaSegmenter);
	        dt = new DiacritizeText(binDir, "", binDir + "all-text.txt.nocase.blm", binDir + "all-text.txt.nocase.dic", farasaSegmenter, farasaPOSTagger);

	    }
	    
	    public static void main(String[] args) throws Exception{
	    	loadFarasa();
	        String text = " القبائل تاريخ قديم بعضها ولمعظم من أيام مملكة سبأ";
	        System.out.println(text.trim());
	    	System.out.println(diacritiseText(text));
	    	
	    	
	        text = "اقبائل اليمن هي القبائل القاطنة ضمن حدود الجمهورية اليمنية";
	        System.out.println(text);
	    	System.out.println(diacritiseText(text));
	    	
	        text = "اقبائل اليمن هي القبائل القاطنة ضمن حدود الجمهورية اليمنية. لا توجد إحصائيات رسمية لكن تشير بعض الدراسات إلى أن القبائل";
	        System.out.println(text);
	        System.out.println(diacritiseText(text));	    	
	    	
	        text = "اقبائل اليمن هي القبائل القاطنة ضمن حدود الجمهورية اليمنية. لا توجد إحصائيات رسمية لكن تشير بعض الدراسات إلى أن القبائل تشكل حوالي من تعداد السكان، وبحسب بعض الإحصائيات فإنه يتواجد ما يقارب  قبيلة في اليمن وبعضها أحصى أكثر من  قبيلة. ولمعظم القبائل تاريخ قديم بعضها من أيام مملكة سبأ";
	        System.out.println(text);
	        System.out.println(diacritiseText(text));	    	

	    	
	        text = "الموسيقى أو الموسيقا هي فن مؤلف من الأصوات والسكوت عبر فترة زمنية، ويعتقد العلماء بأن كلمة الموسيقى يونانية الأصل، وقد كانت تعني سابقا الفنون عموما غير أنها أصبحت فيما بعد تطلق على لغة الألحان فقط. وقد عرفت لفظة موسيقى بأنها فن الألحان وهي صناعة يبحث فيها عن تنظيم الأنغام والعلاقات فيما بينها وعن الإيقاعات وأوزانها. والموسيقى فن يبحث عن طبيعة الأنغام من حيث الاتفاق والتنافر. وتأليف الموسيقى وطريقة أدائها وحتى تعريفها بالأصل تختلف تبعًا للسياق الحضاري والاجتماعي. كما أنّ الموسيقى تعزف بواسطة مختلف الآلات: العضوية (صوت الإنسان، التصفيق) وآلات النفخ (الناي، البوق) والوترية (مثل: العود والقيثارة والكمان)، والإلكترونية (الأورغ). تتفاوت الأداءات الموسيقية بين موسيقى منظمة بشدة في أحيان، إلى موسيقى حرة غير مقيدة بأنظمة في أحيان أخرى، وهي لا تتضمن العزف فقط بل أيضًا القرع في الطبول وموسيقى الهرمونيكا.";
	        System.out.println(text);
	        System.out.println(diacritiseText(text));
	    }
	    public static String diacritiseText(String text) throws ClassNotFoundException, IOException, Exception{ 

	    	String diacritized = "";

	    	text = ArabicUtils.removeDiacritics(text).replace("  ", " ");
	    	

	        	diacritized = dt.diacritize(text);
			    return diacritized;

	        
	        
	    }
	}