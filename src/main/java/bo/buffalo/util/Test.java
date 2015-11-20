package bo.buffalo.util;

import java.awt.Dialog.ModalExclusionType;
import java.util.Arrays;

public class Test {
	
	public static void main(String[] args){
		String[] input = new String[]{"1","2","3","x","5","6","a","baaaaaaasdfasdf","c","10","11","12","13","14","15"};
		//System.out.println("string : "+input);
		separateArray(input);
	}
	
	public static void separateArray(String[] array){
		System.out.println(Arrays.toString(array));
		System.out.println(Arrays.deepToString(array));
		
		
		int aux = array.length/5;
		System.out.println("dimencion: "+aux);
		String[] column1 = new String[aux];
		String[] column2 = new String[aux];
		String[] column3 = new String[aux];
		String[] column4 = new String[aux];
		String[] column5 = new String[aux];
		
		try {
			
			int count = 1;
			int fila = 1;
			for (int i = 0; i < array.length; i++) {
				System.out.println("Pos "+i+": "+array[i]);
				System.out.println("count: "+count);
				if(count==6){
					count=1;
				}
				
				
				if(count==1){
					column1[fila] = array[i];
				}
				
				if(count==2){
					column2[fila] = array[i];
				}
				
				if(count==3){
					column3[fila] = array[i];
				}
				
				if(count==4){
					column4[fila] = array[i];
				}
				
				if(count==5){
					column5[fila] = array[i];
				}
				count++;
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
}
