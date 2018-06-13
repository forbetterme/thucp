package model.CPMRM;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


public class ImportCPUtil {
    JSONObject jsonObjDay = new JSONObject();//创建json格式的数据;//创建json格式的数据
    JSONArray jsonDayArr ;
    JSONObject jsonObjName ;
    JSONObject jsonObjCoreOrders ;
    JSONObject jsonObj ;
    JSONArray jsonArr ;//json格式的数组
    JSONObject jsonObjCoreActivities ;
    JSONObject jsonObjCoreService ;


    public void generate() {
    	try {
	    	 //阶段一
    		 initialize();
	    	 jsonObjName.put("名称", "住院第1 天（急诊室到病房或直接到卒中单元）");

	    	 String[] strsAct = new String[] {"询问病史与体格检查（包括NIHSS评分、GCS 评分及Bathel 评分）",
	    			  "完善病历","医患沟通，交待病情","监测并管理血压（必要时降压）",
	    			  "气道管理：防治误吸，必要时经鼻插管及机械通气","控制体温，可考虑低温治疗、冰帽、冰毯",
	    			  "防治感染、应激性溃疡等并发症","合理使用脱水药物","早期脑疝积极考虑手术治疗","记录会诊意见"};
	    	 jsonObjCoreActivities.put("主要诊疗工作",strsAct);

	    	 String[] strsLong = new String[] {"神经内科疾病护理常规", "一级护理", "低盐低脂饮食","安静卧床","监测生命体征","依据病情下达"};
	    	 String[] strsCur = new String[] {"血常规、尿常规、大便常规",
	    			 "肝肾功能、电解质、血糖、血脂、心肌酶谱、凝血功能、血气分析、感染性疾病筛查",
	    			 "头颅CT 、胸片、心电图",
	    			 "根据病情选择：头颅MRI，CTA、MRA 或DSA，骨髓穿刺、血型（如手术）",
	    			 "根据病情下达病危通知","神经外科会诊"};
	    	 jsonObj.put("长期医嘱",strsLong);
	    	 jsonObj.put("临时医嘱", strsCur);
	         jsonArr.put(jsonObj);//将json格式的数据放到json格式的数组里
	         jsonObjCoreOrders.put("重点医嘱", jsonArr);//再将这个json格式的的数组放到最终的json对象中。

	         String[] strsServ = new String[] {"入院宣教及护理评估","正确执行医嘱","观察患者病情变化"};
	         jsonObjCoreService.put("主要护理工作", strsServ);

	         jsonDayArr.put(jsonObjName);
	         jsonDayArr.put(jsonObjCoreActivities);
	         jsonDayArr.put(jsonObjCoreOrders);
	         jsonDayArr.put(jsonObjCoreService);
	         jsonObjDay.put("1", jsonDayArr);

//	         System.out.println(jsonObjDay.toString());

	         clear();
	       //阶段二
	         initialize();
	         jsonObjName.put("名称", "住院第2 天");

	    	 String[] strsAct1 = new String[] {"主治医师查房，书写上级医师查房记录",
	    			  "评价神经功能状态","评估辅助检查结果",
	    			  "继续防治并发症","必要时多科会诊",
	    			  "开始康复治疗","需手术者转神经外科","记录会诊意见"};
	    	 jsonObjCoreActivities.put("主要诊疗工作",strsAct1);

	    	 String[] strsLong1 = new String[] {"神经内科疾病护理常规", "一级护理", "低盐低脂饮食","安静卧床","监测生命体征","基础疾病用药","依据病情下达"};
	    	 String[] strsCur1 = new String[] {"复查异常化验","复查头CT（必要时）","依据病情需要"};
	    	 jsonObj.put("长期医嘱",strsLong1);
	    	 jsonObj.put("临时医嘱", strsCur1);
	         jsonArr.put(jsonObj);//将json格式的数据放到json格式的数组里
	         jsonObjCoreOrders.put("重点医嘱", jsonArr);//再将这个json格式的的数组放到最终的json对象中。

	         String[] strsServ1 = new String[] {"正确执行医嘱","观察患者病情变化"};
	         jsonObjCoreService.put("主要护理工作", strsServ1);

	         jsonDayArr.put(jsonObjName);
	         jsonDayArr.put(jsonObjCoreActivities);
	         jsonDayArr.put(jsonObjCoreOrders);
	         jsonDayArr.put(jsonObjCoreService);
	         jsonObjDay.put("2", jsonDayArr);

//	         System.out.println(jsonObjDay.toString());
	         clear();
	       //阶段三
	         initialize();
	    	 jsonObjName.put("名称", "住院第3 天");

	    	 String[] strsAct2 = new String[] {"主治医师查房，书写上级医师查房记录",
	    			  "评价神经功能状态", "继续防治并发症","必要时会诊",
	    			  "康复治疗","需手术者转神经外科"};
	    	 jsonObjCoreActivities.put("主要诊疗工作",strsAct2);

	    	 String[] strsLong2 = new String[] {"神经内科疾病护理常规", "一级护理", "低盐低脂饮食","安静卧床","监测生命体征","基础疾病用药","依据病情下达"};
	    	 String[] strsCur2 = new String[] {"异常化验复查","复查头CT（必要时）", "依据病情需要下达"};
	    	 jsonObj.put("长期医嘱",strsLong2);
	    	 jsonObj.put("临时医嘱", strsCur2);
	         jsonArr.put(jsonObj);//将json格式的数据放到json格式的数组里
	         jsonObjCoreOrders.put("重点医嘱", jsonArr);//再将这个json格式的的数组放到最终的json对象中。

	         String[] strsServ2 = new String[] {"正确执行医嘱","观察患者病情变化"};
	         jsonObjCoreService.put("主要护理工作", strsServ2);

	         jsonDayArr.put(jsonObjName);
	         jsonDayArr.put(jsonObjCoreActivities);
	         jsonDayArr.put(jsonObjCoreOrders);
	         jsonDayArr.put(jsonObjCoreService);
	         jsonObjDay.put("3", jsonDayArr);

//	         System.out.println(jsonObjDay.toString());
	         clear();
	       //阶段四
	         initialize();
	    	 jsonObjName.put("名称", "第4-6 天");

	    	 String[] strsAct3 = new String[] {"各级医生查房","评估辅助检查结果",
	    			  "评价神经功能状态", "继续防治并发症","必要时相关科室会诊",
	    			  "康复治疗"};
	    	 jsonObjCoreActivities.put("主要诊疗工作",strsAct3);

	    	 String[] strsLong3 = new String[] {"神经内科疾病护理常规", "一～二级护理", "低盐低脂饮食","安静卧床","基础疾病用药","依据病情下达"};
	    	 String[] strsCur3 = new String[] {"异常检查复查","复查血常规、肾功能、血糖、电解质","必要时复查CT","依据病情需要下达"};
	    	 jsonObj.put("长期医嘱",strsLong3);
	    	 jsonObj.put("临时医嘱", strsCur3);
	         jsonArr.put(jsonObj);//将json格式的数据放到json格式的数组里
	         jsonObjCoreOrders.put("重点医嘱", jsonArr);//再将这个json格式的的数组放到最终的json对象中。

	         String[] strsServ3 = new String[] {"正确执行医嘱","观察患者病情变化"};
	         jsonObjCoreService.put("主要护理工作", strsServ3);

	         jsonDayArr.put(jsonObjName);
	         jsonDayArr.put(jsonObjCoreActivities);
	         jsonDayArr.put(jsonObjCoreOrders);
	         jsonDayArr.put(jsonObjCoreService);
	         jsonObjDay.put("4", jsonDayArr);

//	         System.out.println(jsonObjDay.toString());
	         clear();

	       //阶段五
	         initialize();
	    	 jsonObjName.put("名称", "第7-13 天");

	    	 String[] strsAct4 = new String[] {"通知患者及其家属明天出院","向患者交待出院后注意事项，预约复诊日期",
	    			  "如果患者不能出院，在“病程记录”中说明原因和继续治疗的方案"};
	    	 jsonObjCoreActivities.put("主要诊疗工作",strsAct4);

	    	 String[] strsLong4 = new String[] {"神经内科疾病护理常规", "二～三级护理", "低盐低脂饮食","安静卧床","基础疾病用药","依据病情下达"};
	    	 String[] strsCur4 = new String[] {"异常检查复查","必要时行DSA、CTA、MRA 检查","明日出院"};
	    	 jsonObj.put("长期医嘱",strsLong4);
	    	 jsonObj.put("临时医嘱", strsCur4);
	         jsonArr.put(jsonObj);//将json格式的数据放到json格式的数组里
	         jsonObjCoreOrders.put("重点医嘱", jsonArr);//再将这个json格式的的数组放到最终的json对象中。

	         String[] strsServ4 = new String[] {"正确执行医嘱","观察患者病情变化"};
	         jsonObjCoreService.put("主要护理工作", strsServ4);

	         jsonDayArr.put(jsonObjName);
	         jsonDayArr.put(jsonObjCoreActivities);
	         jsonDayArr.put(jsonObjCoreOrders);
	         jsonDayArr.put(jsonObjCoreService);
	         jsonObjDay.put("5", jsonDayArr);

//	         System.out.println(jsonObjDay.toString());
	         clear();

	       //阶段六
	         initialize();
	    	 jsonObjName.put("名称", "第8-14 天（出院日）");

	    	 String[] strsAct5 = new String[] {"再次向患者及家属介绍病出院后注意事项，出院后治疗及家庭保健","患者办理出院手续，出院"};
	    	 jsonObjCoreActivities.put("主要诊疗工作",strsAct5);

	    	 String[] strsLong5 = new String[] {"通知出院", "依据病情给予出院带药及建议", "出院带药"};
	    	 jsonObj.put("出院医嘱",strsLong5);
	         jsonArr.put(jsonObj);//将json格式的数据放到json格式的数组里
	         jsonObjCoreOrders.put("重点医嘱", jsonArr);//再将这个json格式的的数组放到最终的json对象中。

	         String[] strsServ5 = new String[] {"正确执行医嘱","观察患者病情变化"};
	         jsonObjCoreService.put("主要护理工作", strsServ5);

	         jsonDayArr.put(jsonObjName);
	         jsonDayArr.put(jsonObjCoreActivities);
	         jsonDayArr.put(jsonObjCoreOrders);
	         jsonDayArr.put(jsonObjCoreService);
	         jsonObjDay.put("6", jsonDayArr);

//	         System.out.println(jsonObjDay.toString());
	         clear();
    	}
    	catch (JSONException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    }

    public void initialize(){

        jsonDayArr = new JSONArray();
        jsonObjName = new JSONObject();
        jsonObjCoreOrders = new JSONObject();
        jsonObj = new JSONObject();
        jsonArr = new JSONArray();//json格式的数组
        jsonObjCoreActivities = new JSONObject();
        jsonObjCoreService = new JSONObject();
    }

    public void clear() {
    	jsonDayArr = null;
        jsonObjName = null;
        jsonObjCoreOrders = null;
        jsonObj = null;
        jsonArr = null;
        jsonObjCoreActivities = null;
        jsonObjCoreService = null;
	}

    public JSONObject getJsonStages(){
    	return jsonObjDay;
    }

    public void writeFile(String filePath, String sets)
            throws IOException {
        FileWriter fw = new FileWriter(filePath);
        PrintWriter out = new PrintWriter(fw);
        out.write(sets);
        out.println();
        fw.close();
        out.close();
    }

    public JSONObject readJsonFile(String filepath) throws Exception{
        JSONTokener jsonTokener = new JSONTokener(new FileReader(new File(filepath)));
        JSONObject jsonObject = new JSONObject(jsonTokener);
//        System.out.println(jsonObject);
        return jsonObject;
    }

    public StandardCPStage[] readJsonInput(String filename) throws Exception {
    	JSONObject jsonInput;
		jsonInput = this.readJsonFile(filename);
		StandardCPStage[] CPS = new StandardCPStage[jsonInput.length()];
		for (int i = 0; i < CPS.length; i++) {
			CPS[i] = new StandardCPStage();
		}
//		System.out.println(CPS.length);
		for (int i = 0; i < CPS.length; i++) {
			JSONArray jsarr = jsonInput.getJSONArray(String.valueOf(i+1));
//			System.out.println(jsarr.toString());
			for (int j = 0; j < jsarr.length(); j++) {
				JSONObject obj = jsarr.getJSONObject(j);
				if (obj.keySet().size() == 1 && obj.keySet().contains("名称")) {
					CPS[i].name = obj.getString("名称");
				}
				if (obj.keySet().size() == 1 && obj.keySet().contains("主要诊疗工作")) {
					JSONArray strs = obj.getJSONArray("主要诊疗工作");
					for (int k = 0; k < strs.length(); k++) {
						CPS[i].coreActivities.add(strs.getString(k));
					}
//					System.out.println(CPS[i].coreActivities.toString());
				}
				if (obj.keySet().size() == 1 && obj.keySet().contains("重点医嘱")) {
					JSONArray strs = obj.getJSONArray("重点医嘱");
					for (int k = 0; k < strs.length(); k++) {
						JSONObject objOrder = new JSONObject();
						objOrder = strs.getJSONObject(k);
						if (objOrder.keySet().contains("长期医嘱")) {
							JSONArray strs1 = objOrder.getJSONArray("长期医嘱");
							for (int l = 0; l < strs1.length(); l++) {
								CPS[i].coreOrdersLong.add(strs1.getString(l));
							}
						}
						if (objOrder.keySet().contains("临时医嘱")) {
							JSONArray strs1 = objOrder.getJSONArray("临时医嘱");
							for (int l = 0; l < strs1.length(); l++) {
								CPS[i].coreOrdersCur.add(strs1.getString(l));
							}
						}
						if (objOrder.keySet().contains("出院医嘱")) {
							JSONArray strs1 = objOrder.getJSONArray("出院医嘱");
							for (int l = 0; l < strs1.length(); l++) {
								CPS[i].coreOrdersOut.add(strs1.getString(l));
							}
						}
//						CPS[i].coreActivities.add(strs.getString(k));
					}
//					System.out.println(CPS[i].coreActivities.toString());
				}
				if (obj.keySet().size() == 1 && obj.keySet().contains("主要护理工作")) {
					JSONArray strs = obj.getJSONArray("主要护理工作");
					for (int k = 0; k < strs.length(); k++) {
						CPS[i].coreServices.add(strs.getString(k));
					}
//					System.out.println(CPS[i].coreActivities.toString());
				}
			}
		}
		return CPS;
    }



	public static void main(String[] args) throws Exception {

     	//生成标准临床路径的json文件
		ImportCPUtil imcp = new ImportCPUtil();
		imcp.generate();
		JSONObject jsonData = imcp.getJsonStages();
		imcp.writeFile("./data/CPMRM/test.json", jsonData.toString());

		//读取json文件
		StandardCPStage[] CPS;
		CPS = imcp.readJsonInput("./data/CPMRM/test.json");

		System.out.println(CPS[0].getCoreActivities());
		System.out.println(CPS[0].getCoreOrders());
		System.out.println(CPS[0].getCoreServices());

		System.out.println(CPS.length);

	}


}
