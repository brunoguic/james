/*
 * Copyright 2014 Ghent University, Bayer CropScience.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jamesframework.core.problems;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import org.jamesframework.core.problems.datatypes.SubsetData;
import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.problems.solutions.SubsetSolution;
import org.jamesframework.core.util.SetUtilities;

/**
 * High-level subset problem consisting of subset data, an objective and possibly some constraints (see {@link AbstractProblem}).
 * All items in the data set are identified using a unique integer ID so that any subset selection problem comes down to
 * selection of a subset of these IDs. The solution type is fixed to {@link SubsetSolution} and the data type can be set
 * to any implementation of the {@link SubsetData} interface. When creating the problem, the minimum and maximum allowed
 * subset size are specified. Methods are provided to create random or empty subset solutions, based on the IDs of
 * the items that are retrieved from the underlying subset data.
 * 
 * @param <DataType> underlying data type, should implement the interface {@link SubsetData}
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class SubsetProblem<DataType extends SubsetData> extends AbstractProblem<SubsetSolution, DataType> {

    // minimum and maximum subset size
    private int minSubsetSize, maxSubsetSize;
    
    // indicates whether IDs in the created/copied subset solutions should be sorted
    private final boolean sortedIDs;
    
    /**
     * Creates a new subset problem with given objective, data and minimum/maximum subset size. Both <code>objective</code>
     * and <code>data</code> are not allowed to be <code>null</code>, an exception will be thrown if they are. Any objective
     * designed to evaluate subset solutions (or more general solutions) using the specified data type (or more general data)
     * is accepted. The minimum and maximum subset size should be contained in <code>[0,n]</code> where <code>n</code>
     * is the number of entities in the given subset data from which a subset is to be selected. Also, the minimum size
     * should be smaller than or equal to the maximum size. If <code>sortedIDs</code> is <code>true</code>, any subset
     * solution generated by this problem (random, empty, ...) will store the underlying IDs in sorted sets.
     * 
     * @param objective objective function, can not be <code>null</code>
     * @param data underlying subset data, can not be <code>null</code>
     * @param minSubsetSize minimum subset size (should be &ge; 0 and &le; maximum subset size)
     * @param maxSubsetSize maximum subset size (should be &ge; minimum subset size and &le; number of entities in underlying data)
     * @param sortedIDs indicates whether IDs are sorted in generated subset solutions
     * 
     * @throws NullPointerException if <code>objective</code> or <code>data</code> is <code>null</code>
     * @throws IllegalArgumentException if an invalid minimum or maximum subset size is specified
     */
    public SubsetProblem(Objective<? super SubsetSolution, ? super DataType> objective,
                                DataType data, int minSubsetSize, int maxSubsetSize, boolean sortedIDs) {
        // call constructor of AbstractProblem (already checks that objective is not null)
        super(objective, data);
        // check that data is not null
        if(data == null){
            throw new NullPointerException("Error while creating subset problem: subset data is required, can not be null.");
        }
        // check constraints on minimum/maximum size
        if(minSubsetSize < 0){
            throw new IllegalArgumentException("Error while creating subset problem: minimum subset size should be >= 0.");
        }
        if(maxSubsetSize > data.getIDs().size()){
            throw new IllegalArgumentException("Error while creating subset problem: maximum subset size can not be larger "
                                                + "than number of entities in underlying subset data.");
        }
        if(minSubsetSize > maxSubsetSize){
            throw new IllegalArgumentException("Error while creating subset problem: minimum subset size should be <= maximum subset size.");
        }
        // store min/max size
        this.minSubsetSize = minSubsetSize;
        this.maxSubsetSize = maxSubsetSize;
        // store sorted
        this.sortedIDs = sortedIDs;
    }
    
    /**
     * Creates a new subset problem with given objective, data and minimum/maximum subset size. Both <code>objective</code>
     * and <code>data</code> are not allowed to be <code>null</code>, an exception will be thrown if they are. Any objective
     * designed to evaluate subset solutions (or more general solutions) using the specified data type (or more general data)
     * is accepted. The minimum and maximum subset size should be contained in <code>[0,n]</code> where <code>n</code>
     * is the number of entities in the given subset data from which a subset is to be selected. Also, the minimum size
     * should be smaller than or equal to the maximum size. Generated subset solutions do not guarantee any order of IDs.
     * 
     * @param objective objective function, can not be <code>null</code>
     * @param data underlying subset data, can not be <code>null</code>
     * @param minSubsetSize minimum subset size (should be &ge; 0 and &le; maximum subset size)
     * @param maxSubsetSize maximum subset size (should be &ge; minimum subset size and &le; number of entities in underlying data)
     * @throws NullPointerException if <code>objective</code> or <code>data</code> is <code>null</code>
     * @throws IllegalArgumentException if an invalid minimum or maximum subset size is specified
     */
    public SubsetProblem(Objective<? super SubsetSolution, ? super DataType> objective,
                                DataType data, int minSubsetSize, int maxSubsetSize) {
        this(objective, data, minSubsetSize, maxSubsetSize, false);
    }
    
    /**
     * Creates a subset problem with fixed subset size. Equivalent to calling<pre>
     * SubsetProblem p = new SubsetProblem(objective, data, fixedSubsetSize, fixedSubsetSize);</pre>
     * The fixed subset size should be contained in <code>[0,n]</code> where <code>n</code>
     * is the number of entities in the given subset data from which a subset is to be selected.
     * Generated subset solutions do not guarantee any order of IDs.
     * 
     * @param objective objective function, can not be <code>null</code>
     * @param data underlying subset data, can not be <code>null</code>
     * @param fixedSubsetSize fixed subset size
     * @throws NullPointerException if <code>objective</code> or <code>data</code> is <code>null</code>
     * @throws IllegalArgumentException if an invalid fixed subset size is specified 
     */
    public SubsetProblem(Objective<? super SubsetSolution, ? super DataType> objective, DataType data, int fixedSubsetSize) {
        this(objective, data, fixedSubsetSize, fixedSubsetSize);
    }
    
    /**
     * Set new subset data, verifying that the data is not <code>null</code>.
     * 
     * @param data new subset data, can not be <code>null</code>
     * @throws NullPointerException if <code>data</code> is <code>null</code>
     */
    @Override
    public void setData(DataType data) {
        // check not null
        if(data == null){
            throw new NullPointerException("Error while setting data in subset problem: subset data can not be null.");
        }
        // not null: call super
        super.setData(data);
    }
    
    /**
     * Create a random solution within the allowed minimum and maximum subset size. The IDs of all entities
     * are taken from the underlying subset data, and a random subset of IDs is selected.
     * 
     * @return random subset solution within minimum and maximum size
     */
    @Override
    public SubsetSolution createRandomSolution() {
        // get thread local random generator
        final Random rg = ThreadLocalRandom.current();
        // create new subset solution with IDs from underlying data
        final SubsetSolution sol = new SubsetSolution(getData().getIDs(), sortedIDs);
        // pick random number of initially selected IDs within bounds
        int size = minSubsetSize + rg.nextInt(maxSubsetSize-minSubsetSize+1);
        // randomly select initial IDs
        sol.selectAll(SetUtilities.getRandomSubset(sol.getAllIDs(), size, rg));
        // return random solution
        return sol;
    }
    
    /**
     * Creates an empty subset solution in which no IDs are selected. The set of all IDs is obtained from
     * the underlying data and passed to the created empty solution.
     *
     * @return empty subset solution with no selected IDs
     */
    public SubsetSolution createEmptySubsetSolution(){
        return new SubsetSolution(getData().getIDs(), sortedIDs);
    }
    
    /**
     * Checks whether the given subset solution is rejected. A subset solution is rejected if any of the
     * rejecting constraints is violated (see {@link #addRejectingConstraint(Constraint)}) or if it has
     * an invalid size (number of selected IDs).
     * 
     * @param solution subset solution to verify
     * @return <code>true</code> if the solution is rejected
     */
    @Override
    public boolean rejectSolution(SubsetSolution solution){
        return rejectSolution(solution, true);
    }
    
    /**
     * Checks whether the given subset solution is rejected. A subset solution is rejected if any of
     * the rejecting constraints is violated (see {@link #addRejectingConstraint(Constraint)}). Only
     * when <code>checkSubsetSize</code> is <code>true</code> the solution is also rejected if it has
     * an invalid size.
     * 
     * @param solution subset solution to verify
     * @param checkSubsetSize indicates whether a solution should be rejected if it has an invalid size
     * @return <code>true</code> if the solution is rejected
     */
    public boolean rejectSolution(SubsetSolution solution, boolean checkSubsetSize){
        if(checkSubsetSize){
            return solution.getNumSelectedIDs() < getMinSubsetSize()        // too small
                    || solution.getNumSelectedIDs() > getMaxSubsetSize()    // too large
                    || super.rejectSolution(solution);                      // violates rejecting constraint
        } else {
            // ignore size
            return super.rejectSolution(solution);
        }
    }

    /**
     * Get the minimum subset size.
     * 
     * @return minimum subset size
     */
    public int getMinSubsetSize() {
        return minSubsetSize;
    }

    /**
     * Set the minimum subset size. Specified size should be &ge; 1 and &le; the current maximum subset size.
     * 
     * @param minSubsetSize new minimum subset size
     * @throws IllegalArgumentException if an invalid minimum size is given
     */
    public void setMinSubsetSize(int minSubsetSize) {
        // check size
        if(minSubsetSize <= 0){
            throw new IllegalArgumentException("Error while setting minimum subset size: should be > 0.");
        }
        if(minSubsetSize > maxSubsetSize){
            throw new IllegalArgumentException("Error while setting minimum subset size: should be <= maximum subset size.");
        }
        this.minSubsetSize = minSubsetSize;
    }

    /**
     * Get the maximum subset size.
     * 
     * @return maximum subset size
     */
    public int getMaxSubsetSize() {
        return maxSubsetSize;
    }

    /**
     * Set the maximum subset size. Specified size should be &ge; the current minimum subset size
     * and &le; the number of entities in the underlying subset data.
     * 
     * @param maxSubsetSize new maximum subset size
     * @throws IllegalArgumentException if an invalid maximum size is given
     */
    public void setMaxSubsetSize(int maxSubsetSize) {
        // check size
        if(maxSubsetSize < minSubsetSize){
            throw new IllegalArgumentException("Error while setting maximum subset size: should be >= minimum subset size.");
        }
        if(maxSubsetSize > getData().getIDs().size()){
            throw new IllegalArgumentException("Error while setting maximum subset size: can not be larger "
                                                + "than number of entities in underlying subset data.");
        }
        this.maxSubsetSize = maxSubsetSize;
    }

}
