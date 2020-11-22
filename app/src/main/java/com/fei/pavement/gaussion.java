package com.fei.pavement;
import java.util.ArrayList;
import java.util.List;

//outData��v_data Ϊһ��double���͵����飬����Ϊ�̶�Ϊ400
class gaussion {
//	12000
	private int MAX_SIZE=1000;
	private int MAX_SPEED=20;
	public List<Integer> show(double[] outData, double[] v_data) {
		double averagedata = average(outData);//����ƽ��ֵ����
		double deviationdata = deviation(outData);//���ñ�׼���
//		System.out.println();
		List<Integer> list=new ArrayList<Integer>();//���ø÷��������������Ԫ��
		//��outData��v_data�е�Ԫ�ؽ���һϵ���жϣ�Ȼ��ͨ���жϵ�Ԫ�ص���ż�������list
		for (int i=0;i<MAX_SIZE;i++) {
			if (Math.abs(outData[i]-averagedata)>v_data[i]/8*deviationdata){
				if (Math.abs(outData[i])>v_data[i]*0.04) {
					if (v_data[i]>MAX_SPEED) {
//						String s = String.valueOf(i);//��int��ת��Ϊstring��
//						System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
						list.add(i);
					}
				}
			}
		}
		return list;
	}

	//����һ�������������ƽ��ֵ
	private double average(double[] outData) {
		double result = 0;
		for(int i = 0;i < MAX_SIZE; i++) {
			result += outData[i] / MAX_SIZE;
		}
		return result;
	}

	//����һ�������������׼��
	private double deviation(double[] outData) {
		double result = 0;
		for (int i = 0; i < MAX_SIZE; i++) {
			result = result + (Math.pow((outData[i] - average(outData)), 2));
		}
		result = Math.sqrt(result / (MAX_SIZE-1));
		return result;
	}
}