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

package org.jamesframework.core.search.listeners;

import org.jamesframework.core.exceptions.IncompatibleSearchListenerException;
import org.jamesframework.core.problems.Solution;
import org.jamesframework.core.problems.constraints.Validation;
import org.jamesframework.core.problems.objectives.Evaluation;
import org.jamesframework.core.search.LocalSearch;

/**
 * <p>
 * Extends the general search listener interface with a local search specific callback, fired when the current solution
 * has been modified. A listener implementing this interface may still be attached to a general search, in which case
 * the specific callback will simply never be fired. If attached to a local search, it will be fired whenever a new
 * current solution has been adopted.
 * </p>
 * <p>
 * The additional callback also has a default empty implementation.
 * </p>
 * 
 * @param <SolutionType> solution type, required to extend {@link Solution} 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public interface LocalSearchListener<SolutionType extends Solution> extends SearchListener<SolutionType> {

    /**
     * Fired whenever a new current solution has been adopted. Called exactly once for every modification to the
     * current solution.
     * 
     * @param search local search which has updated its current solution
     * @param newCurrentSolution newly adopted current solution
     * @param newCurrentSolutionEvaluation evaluation of new current solution
     * @param newCurrentSolutionEvaluation validation of new current solution
     * @throws IncompatibleSearchListenerException if the listener is not compatible with the search
     */
    default public void modifiedCurrentSolution(LocalSearch<? extends SolutionType> search,
                                                SolutionType newCurrentSolution,
                                                Evaluation newCurrentSolutionEvaluation,
                                                Validation newCurrentSolutionValidation){}
    
}
