//  Copyright 2014 Herman De Beukelaer
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

package org.jamesframework.core.problems.objectives;

import org.jamesframework.core.problems.solutions.Solution;

/**
 * Abstract objective that handles minimization/maximization settings.
 * 
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 * @param <SolutionType> solution type to be evaluated, required to extend {@link Solution}
 * @param <DataType> underlying data type
 */
public abstract class AbstractObjective<SolutionType extends Solution, DataType> implements Objective<SolutionType, DataType>{
    
    // indicates whether the objective is minimizing
    private boolean minimizing;

    @Override
    public final boolean isMinimizing() {
        return minimizing;
    }
    
    /**
     * Turn this objective into a minimizing objective.
     */
    public final void setMinimizing(){
        minimizing = true;
    }
    
    /**
     * Turn this objective into a maximizing objective.
     */
    public final void setMaximizing(){
        minimizing = false;
    }

}