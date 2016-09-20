import java.util.Arrays;
public class CustomerServiceRepScores
{
    private int repQuantity;
    private int numberOfPossibleScores;
    private int[][] scores;
    private int[][] last20;
    private int[][] counter;
    private double[] averages;


    public void CustomerServiceRepScores(int repQuantity, int scoreQuantity)
    {
        this.repQuantity = repQuantity;
        this.numberOfPossibleScores = scoreQuantity;
        this.scores = new int[this.repQuantity][this.numberOfPossibleScores];
        //initialize all score counts to zero
        for(int i = 0; i < this.scores.length; i++) {
            Arrays.fill(this.scores[i],0);
        }
        this.last20 = new int[this.repQuantity][20];
        //initialize all score counts to zero
        for(int i = 0; i < this.last20.length; i++) {
            Arrays.fill(this.last20[i],0);
        }
        this.counter = new int[this.repQuantity][2];
        //initialize all score counts to zero
        for(int i = 0; i < this.counter.length; i++) {
            Arrays.fill(this.counter[i],0);
        }
        this.averages = new double[this.repQuantity];
        //initialize all score counts to zero
        for(int i = 0; i < this.averages.length; i++) {
            Arrays.fill(this.averages,0);
        }
    }
    
    /**
    *
    * @param repID the representative who received this score.
    * @param score the score received
    */
    public void addNewScore(int repID, int score)
    {
        this.scores[repID][score-1] += 1;
        updateLast20(repID, score);
        updateAverage(repID);
    }
    
    private void updateLast20(int repID, int score)
    {
        last20[repID][counter[repID][0]] = score;
        updateCounter(repID);
    }
    
    private void updateCounter(int repID)
    {
        if(counter[repID][0] == 19) {
            counter[repID][0] = 0;
            counter[repID][1] = 1; 
        }
        else{
            counter[repID][0]++;
        }
    }
    
    private void updateAverage(int repID)
    {
        if(counter[repID][1] == 1) {
            double oldAvg = averages[repID];
            double repTotal = 0;
            for(int i = 0; i < 20; i++) {
                repTotal += last20[repID][i];
            }
            double newAvg = repTotal / 20;
            averages[repID] = newAvg;
            if(newAvg < 2.5 && oldAvg >= 2.5) {
                System.out.println("Representative " + repID + "'s running average has dropped to " + newAvg + ".");
            }
        }
    }
    
    /**
    *
    * @param repID the id of the rep
    * @return an array of length this.numberOfPossibleScores with the current score totals for the rep
    */
    public int[] getCumulativeScoreForRep(int repID)
    {
        return Arrays.copyOf(this.scores[repID], this.scores[repID].length);
    }

    /**
     * 
     *
     * @param repID the id of the rep
     * @return the average of the last 20 scores of a rep
     */
    public double getAverage(int repID)
    {
        if(counter[repID][1] == 0) {
            System.out.println("This representative has not yet received 20 scores.");
            return 0.0;
        }
        else {
            return averages[repID];
        }
    }

    /**
     *
     *
     * @param repID the id of the rep
     * @return an array of doubles that contains the cumulative scores of the rep with an
     * additional place for the average of the rep's last 20 scores.
     */
    public double[] getAvgWithCumulativeScore(int repID)
    {
        double[] cumScoreWithAvg = new double[scores[repID].length + 1];
        for(int i = 0; i < scores[repID].length - 1; i++) {
            cumScoreWithAvg[i] = scores[repID][i];
        }
        if(counter[repID][1] == 1) {
            cumScoreWithAvg[cumScoreWithAvg.length - 1] = averages[repID];
        }
        else {
            cumScoreWithAvg[cumScoreWithAvg.length - 1] = -1.0;
        }
        return cumScoreWithAvg;
    }

    /**
     * Resets the scores and aaverage of a rep.
     *
     * @param repID the id of the rep
     */
    public void resetRep(int repID)
    {
        Arrays.fill(this.scores[repID],0);
        Arrays.fill(this.last20[repID],0);
        Arrays.fill(this.counter[repID],0);
        averages[repID] = 0.0;
    }

    /**
     * Resets scores and averages of all reps.
     *
     */
    public void resetAll()
    {
        for(int i = 0; i < this.scores.length; i++) {
            Arrays.fill(this.scores[i],0);
        }
        for(int i = 0; i < this.last20.length; i++) {
            Arrays.fill(this.last20[i],0);
        }
        for(int i = 0; i < this.counter.length; i++) {
            Arrays.fill(this.counter[i],0);
        }
        for(int i = 0; i < this.averages.length; i++) {
            Arrays.fill(this.averages,0);
        }
    }
    
}