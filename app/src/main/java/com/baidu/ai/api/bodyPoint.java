package com.baidu.ai.api;


import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class bodyPoint {
    public boolean hasNoVaule=true;
    public int[][] POSE_PAIRS={
            {10,0},{0,5},{0,15},{15,11},{5,12},
            {0,9},{9,14},{9,20},{14,7},{7,4},{20,13},
            {13,19},{9,18},{18,1},{1,3},{9,2},{2,8},{8,17}
    };
    public  final int left_hip=0;
    public  final int top_head=1;
    public  final int right_mouth_corner=2;
    public  final int neck=3;
    public  final int left_shoulder=4;
    public  final int left_knee=5;
    public  final int left_ankle=6;
    public  final int left_mouth_corner=7;
    public  final int right_elbow=8;
    public  final int right_ear=9;
    public  final int nose=10;
    public  final int left_eye=11;
    public  final int right_eye=12;
    public  final int right_hip=13;
    public  final int left_wrist=14;
    public  final int left_eat=15;
    public  final int left_elbow=16;
    public  final int right_shoulder=17;
    public  final int right_ankle=18;
    public  final int right_knee=19;
    public  final int right_wrist=20;
    public Parts[] parts=new Parts[21];
    String[] name=new String[]{"nose","right_knee","left_hip","right_ankle",
            "right_wrist","left_eye","left_mouth_corner",
            "right_elbow","left_knee","neck","top_head",
            "right_ear","left_ear","left_elbow","right_shoulder","right_eye",
            "right_mouth_corner","left_ankle","right_hip","left_wrist","left_shoulder"};
    public int people_num(String str){
            String[] array=str.replaceAll("\\s+","")
                    .split("\\[");
            String[] array2=array[0].split("\\:|,");
            int num=Integer.parseInt(array2[1]);
            return num;


    }
    public void myspilt(String str){
        //String str="result:{\"person_num\": 1, \"person_info\": [{\"body_parts\": {\"left_hip\": {\"y\": 527.44140625, \"x\": 355.04296875, \"score\": 0.651006281375885}, \"top_head\": {\"y\": 25.13674163818359, \"x\": 263.71484375, \"score\": 0.8308587074279785}, \"right_mouth_corner\": {\"y\": 162.1289215087891, \"x\": 224.57421875, \"score\": 0.8779381513595581}, \"neck\": {\"y\": 188.2226715087891, \"x\": 276.76171875, \"score\": 0.7961496710777283}, \"left_shoulder\": {\"y\": 214.3164215087891, \"x\": 355.04296875, \"score\": 0.8395131230354309}, \"left_knee\": {\"y\": 631.81640625, \"x\": 165.8632965087891, \"score\": 0.7670961618423462}, \"left_ankle\": {\"y\": 618.76953125, \"x\": 361.56640625, \"score\": 0.4249553680419922}, \"left_mouth_corner\": {\"y\": 155.6054840087891, \"x\": 257.19140625, \"score\": 0.883640706539154}, \"right_elbow\": {\"y\": 377.40234375, \"x\": 211.52734375, \"score\": 0.8536253571510315}, \"right_ear\": {\"y\": 109.9414215087891, \"x\": 205.00390625, \"score\": 0.7496447563171387}, \"nose\": {\"y\": 136.0351715087891, \"x\": 231.09765625, \"score\": 0.8832266926765442}, \"left_eye\": {\"y\": 103.4179840087891, \"x\": 263.71484375, \"score\": 0.8677999973297119}, \"right_eye\": {\"y\": 109.9414215087891, \"x\": 224.57421875, \"score\": 0.8945008516311646}, \"right_hip\": {\"y\": 507.87109375, \"x\": 270.23828125, \"score\": 0.6680450439453125}, \"left_wrist\": {\"y\": 579.62890625, \"x\": 361.56640625, \"score\": 0.8698246479034424}, \"left_ear\": {\"y\": 116.4648590087891, \"x\": 315.90234375, \"score\": 0.8849414587020874}, \"left_elbow\": {\"y\": 403.49609375, \"x\": 335.47265625, \"score\": 0.8294979929924011}, \"right_shoulder\": {\"y\": 214.3164215087891, \"x\": 205.00390625, \"score\": 0.793376088142395}, \"right_ankle\": {\"y\": 592.67578125, \"x\": 315.90234375, \"score\": 0.2949504852294922}, \"right_knee\": {\"y\": 592.67578125, \"x\": 87.58204650878906, \"score\": 0.837354302406311}, \"right_wrist\": {\"y\": 520.91796875, \"x\": 185.43359375, \"score\": 0.7566587924957275}}, \"location\": {\"height\": 667.8069458007812, \"width\": 450.2604370117188, \"top\": 1.19305419921875, \"score\": 0.9987338185310364, \"left\": 48.73957824707031}}], \"log_id\": 1823195431547637945}\n";
        String[] array=str.replaceAll("\\s+","")
                .split("\\[");
        String[] array1=array[1].split("[\\{\\}]");
        int j=0;
        for(int i=2;i<44;i=i+2){
            String[] Pointxy=array1[i+1].split("((,?\\\"[a-z]+\\\"\\:))");
            //System.out.println(name[j]);
            parts[j]=new Parts();
            parts[j].name=name[j];
            parts[j].y=Double.parseDouble(Pointxy[3]);
            parts[j].x=Double.parseDouble(Pointxy[2]);
            parts[j].socre=Double.parseDouble(Pointxy[1]);
            j++;
        }
    }
    //（正面照片）
    public  String cauculate1(){
        double left_shoulder=parts[PartNum.left_shoulder].y;
        double right_shoulder=parts[PartNum.right_shoulder].y;
        double top_head=parts[PartNum.top_head].y;
        double left_ankle=parts[PartNum.left_ankle].y;
        double right_ankle=parts[PartNum.right_ankle].y;
        double middlepoint=(left_ankle+right_ankle)/2;
        double cau=Math.abs(left_shoulder-right_shoulder)/Math.abs(top_head-middlepoint);
        if (cau>0.01){
            return "可能是高低肩\n"+"矫正：\n"  +
                    "1、可以尽量减少会造成高低肩的行为，特别是长期使用同一只手进行干活。\n" +
                    "2、可以配合呼吸做提肩胛骨放松的动作。先双肩向上提吸气维持一段时间，然后放气，一组20个，一天两组。可以适当地有意识地将肩膀的肌肉做同等的提升，另外两脚分开与肩同宽，上体直立，两手持哑铃下垂于体侧，然后吸气，同时两手用力做哑铃侧平举训练，可以使两侧肩膀的肌肉得到同等的锻炼。\n" +
                    "3、在日常生活中保持端正的姿势也很重要，站立的时候不要歪歪扭扭。\n" ;
        }
        else{
            return "不是高低肩\n";
        }
    }

    public  String cauculate2(){
        double left_hip_x=parts[PartNum.left_hip].x;
        double left_hip_y=parts[PartNum.left_hip].y;
        double left_knee_x=parts[PartNum.left_knee].x;
        double left_knee_y=parts[PartNum.left_knee].y;
        double left_ankle_x=parts[PartNum.left_ankle].x;
        double left_ankle_y=parts[PartNum.left_ankle].y;
        double cau1 = Math.atan2((left_knee_y-left_hip_y), (left_hip_x-left_knee_x));
        double cau2 = Math.PI-Math.atan2((left_ankle_y-left_knee_y), (left_ankle_x-left_knee_x));
        if (cau1+cau2>Math.PI){
            return "可能是X型腿\n"+"矫正：如果要矫正的话，在婴幼儿时期，越早纠正，效果是越好的，一般可以通过去专门的机构或者是医院定做一些矫形的支具佩戴。长期佩戴的话，在婴幼儿生长发育期间，能够逐渐把进行纠正。但是另外也要病因治疗，如果是因为缺乏钙或者缺乏维生素D的话，必须要适当的补充相关的一些缺乏的物质，才能够解决根本的原因，但是如果是已经发育到18岁以上的一些成年人的话，那么就必须要通过手术治疗来矫正畸形。平时可以做的有益于矫正体型的动作有：举腿、开合蹲起、仰卧夹腿等。\n";
        }
        if(cau1+cau2<Math.PI){
            return "可能是O型腿\n"+"矫正：如果出现明显的O型腿，一定要先确定O型腿的原因，再进行矫正治疗。很多儿童和青少年是因为维生素D缺乏导致佝偻病，或者单纯的维生素D和钙质处于相对缺乏的状态，从而引发O型腿畸形。一般需要及时补充钙质和维生素D，并且及时进行腿部夹板外固定，才能有效矫正O型腿。如果是因为青少年走路习惯导致的O型腿，尤其是外八字脚走路的青少年，一定要早期穿矫正鞋，矫正外八字脚的症状，并且适当的绑腿，进行膝关节的矫正，才能逐渐使O型腿的症状消失。如果是成年人，已经完成了生长发育，只能通过手术的办法矫正O型腿畸形。\n";
        }
        else  return "没有腿型问题\n";
    }

    public  String cauculate3() {
        double left_hip_y = parts[PartNum.left_hip].y;
        double left_knee_y = parts[PartNum.left_knee].y;
        double right_hip_y = parts[PartNum.right_hip].y;
        double right_knee_y = parts[PartNum.right_knee].y;
        if(left_hip_y!=right_hip_y||left_knee_y!=right_knee_y){
            return "可能是长短腿\n"+"1、结构性长短腿。由于骨骼真正短缩所致，通常与先天性疾病或者后天外伤等有关。\n"
                    +"矫正：一般的训练可以改善骨盆偏移、调整动作模式，但无法从根本上矫正结构性长短腿的问题，需要辅具或手术。\n"
                    +"2.功能性长短腿：长短腿其实大部分都是功能性的，虽不是真正的腿长不等，但腿长有左右不同的现象就会造成身体姿势异常，生物力学的改变，很容易引起其他关节、肌肉的病变，进而扭曲、压迫筋膜、神经造成内脏相关疾病。\n"
                    +"矫正：要解决功能性长短腿，我们先要评估分类，无非就是骨头和肌肉的问题，然后根据具体原因针对性的进行矫正,通过正骨和运动康复干预。\n"
                    +"(长短腿在4mm以内没有出现体态异常，也没有任何症状可以，可以不作处理。)\n";
        }
        else return "不是长短腿\n";
    }

    public  String cauculate4() {
        double neck_x = parts[PartNum.neck].x;
        double nose_x = parts[PartNum.nose].x;
        double left_ear_x = parts[PartNum.left_ear].x;
        double right_ear_x = parts[PartNum.right_ear].x;
        double ear_middlex=(left_ear_x + right_ear_x)/2;
        double n=Math.abs((neck_x-ear_middlex)/(nose_x-neck_x));
        if(n>0.1){
            return "可能存在头前伸问题\n"+"矫正：头前伸也叫做颈椎生理曲度变直或者颈椎反弓，出现这种情况进行矫正，主要是两种方法：\n"+
                    "1、在平时坐位的时候，尽量选择头略为后仰，这样就需要做带有靠背的座椅，并且要桌面和电脑尽量高一些，双侧肘关节要有支撑，不能悬空，这样才能够保持头略微的后仰，维持颈椎正常的生理曲度。随着时间的推移，这样就能够矫正生理曲度变直以及反弓的情况。\n"
                    +"2、在夜间睡眠的时候枕头不宜过高，通常选择一拳高左右的枕头，最好是乳胶枕，垫在后脑勺靠下的位置，保持颈椎正常的生理曲度，如果枕头过高或过低都有可能会导致颈椎出现劳损、炎症病灶，导致头部前伸的情况无法矫正。\n";
        }
        else return "无头前伸问题\n";
    }

    public  String cauculate5() {
        double middle_hip_x = (parts[PartNum.left_hip].x + parts[PartNum.right_hip].x) / 2;
        double middle_hip_y = (parts[PartNum.left_hip].y + parts[PartNum.right_hip].y) / 2;
        double middle_knee_x = (parts[PartNum.left_knee].x + parts[PartNum.right_knee].x) / 2;
        double middle_knee_y = (parts[PartNum.left_knee].y + parts[PartNum.right_knee].y) / 2;
        double middle_ankle_x = (parts[PartNum.left_ankle].x + parts[PartNum.right_ankle].x) / 2;
        double middle_ankle_y = (parts[PartNum.left_ankle].y + parts[PartNum.right_ankle].y) / 2;
        double cau1 = Math.abs(Math.atan2((middle_hip_y - middle_knee_y), (middle_hip_x - middle_knee_x)));
        double cau2 = Math.abs(Math.atan2((middle_ankle_y - middle_knee_y), (middle_ankle_x - middle_knee_x)));
        double cau = cau1 + cau2;
        if (cau < (Math.PI * 175 / 180)||cau>(Math.PI*185/180) ){
            return "可能是膝超伸\n"+"矫正：\n"+"1、如果是因为先天性的膝超伸，这种情况一般采用支具矫形的办法，逐渐康复，佩戴支具的时间至少应在3个月以上，每3个月都要复查一次膝关节的X线片或者核磁共振。\n"
                    +"2、如果是因为外伤所导致的膝超伸，这种情况一般合并有膝关节韧带或者半月板的损伤，甚至可能有骨折。这时正确的处理办法应该是尽快完善CT或者核磁共振等检查，明确过伸的根本原因，针对主要原因对症治疗，后期还要加强功能锻炼才能达到治愈的目的。\n";
        }
        else return "无膝超伸\n";
    }

    public void drawPoint(Mat frame)
    {   for(int i=0;i<parts.length;i++) {
        double x,y;
        x = parts[i].x;
        y = parts[i].y;
        if (x <= 0 || y <= 0) {
            continue;
        }
        Imgproc.circle(frame, new Point(x, y), 2, new Scalar(22, 233, 99), 2);
    }

    }
    public void drawSkeleton(Mat frame)
    {
        for(int n=0;n<18;n++) {
            double x, y;
            x = parts[POSE_PAIRS[n][0]].x;
            y = parts[POSE_PAIRS[n][0]].y;
            Point partA = new Point(x, y);
            x = parts[POSE_PAIRS[n][1]].x;
            y = parts[POSE_PAIRS[n][1]].y;
            Point partB = new Point(x, y);
            if (partA.x <= 0 || partA.y <= 0 || partB.x <= 0 || partB.y <= 0) {
                continue;
            }
            Imgproc.line(frame, partA, partB, new Scalar(0, 32, 255),2);
        }
    }

}
