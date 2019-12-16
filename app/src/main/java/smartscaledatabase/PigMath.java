package smartscaledatabase;

public class PigMath {

    private double foodAmount;
    private double ageInDays;
    private static double OPT_SELLING_AGE = 161;

 public  double getAgeInDays(double weight){
     double ageInDays = (weight + 30.16)/0.9;
     return this.ageInDays = ageInDays;
 }

 //FIXME: return part of else statement
 public double estimateReqFood(){

        if (ageInDays >= 133 && ageInDays < 161){
            return foodAmount = ((OPT_SELLING_AGE - ageInDays) * 2975)/1000;
        }
        if (ageInDays >=100 && ageInDays < 133){
            return foodAmount = (((133 - ageInDays) * 2450) + ((OPT_SELLING_AGE - 133) * 2975))/1000;
        }
        else {
            return 1;
        }
     }
}
