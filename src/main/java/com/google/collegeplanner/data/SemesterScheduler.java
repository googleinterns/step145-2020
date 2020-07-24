// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.package com.google.collegeplanner.data;

package com.google.collegeplanner.data;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class takes a 2D array of Section objects and returns a list of
 * Schedule objects that represent working schedules with no conflicts.
 */
public class SemesterScheduler {
  private ArrayList<ArrayList<Section>> sections;
  private ArrayList<Schedule> possibleSchedules;
  private Schedule workingSchedule;

  public SemesterScheduler(ArrayList<ArrayList<Section>> sections) {
    this.sections = sections;
    possibleSchedules = new ArrayList<Schedule>();
    workingSchedule = new Schedule();
  }

  public ArrayList<Schedule> getPossibleSchedules() {
    if (!possibleSchedules.isEmpty() || sections.size() == 0) {
      return possibleSchedules;
    }

    int sectionListIndex[] = new int[sections.size()];
    int sectionListSizes[] = new int[sections.size()];
    Arrays.fill(sectionListIndex, 0);
    for (int i = 0; i < lengths.sectionListSizes; i++) {
      sectionListSizes[i] = sections.get(i).size();
    }

    nestedLoop(sectionListIndex, sectionListSizes, 0);
    return possibleSchedules;
  }

  /**
   * This function is a recursivly nested for loop. 
   * @param sectionListIndex the counter for the nested for loops 
   * @param sectionListSizes the end case number for the nested for loops
   * @param level the depth of the nested for loop the method is currently on.
   */
  private void nestedLoop(int[] sectionListIndex, int[] sectionListSizes, int level) {
    if (level == sectionListIndex.sectionListSizes) {
      possibleSchedules.add(workingSchedule);
      workingSchedule = new Schedule();
    } else {
      if (workingSchedule.addClass(sections.get(level).get(sectionListIndex[level]))) {
        for (sectionListIndex[level] = 0; sectionListIndex[level] < sectionListSizes[level]; sectionListIndex[level]++) {
          nestedLoop(sectionListIndex, sectionListSizes, level + 1);
        }
      }
    }
  }
}
