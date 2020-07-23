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

public final class SemesterScheduler {
  private ArrayList<ArrayList<Section>> sections;
  private ArrayList<Schedule> possibleSchedules;

  public SemesterScheduler(ArrayList<ArrayList<Section>> sections) {
    this.sections = sections;
    possibleSchedules = new ArrayList<Schedule>();
  }

  public ArrayList<Schedule> getPossibleSchedules() {
    if (!possibleSchedules.isEmpty()) {
      return possibleSchedules;
    }
    int counters[] = new int[sections.size()];
    int lengths[] = new int[sections.size()];
    Arrays.fill(counters, 0);
    for (int i = 0; i < lengths.length; i++) {
      lengths[i] = sections.get(i).size();
    }
    nestedLoop(counters, lengths, 0);
    return possibleSchedules;
  }

  /*
   * This function is a recursivly nested for loop. counters[] represents the
   * index which a for loop is on while length[] represents the end of each for
   * loop. counters[] and length[] have the same length, which is the depth of
   * the nested for loop. level is how deep the recursive for loop is in.
   */
  private void nestedLoop(int[] counters, int[] length, int level) {
    if (level == counters.length) {
      testCombinationOfSections(counters);
    } else {
      for (counters[level] = 0; counters[level] < length[level]; counters[level]++) {
        nestedLoop(counters, length, level + 1);
      }
    }
  }

  /*
   * This method tests a combination of sections to see if they conflict with
   * one another. If there are no conflicts, they are added into the list of
   * possible schedules.
   */
  private void testCombinationOfSections(int[] indices) {
    Schedule workingSchedule = new Schedule();

    // Going through each class in the combination and adding it into the schedule
    for (int i = 0; i < indices.length; i++) {
      if (!workingSchedule.addClass(sections.get(i).get(indices[i]))) {
        return;
      }
    }

    possibleSchedules.add(workingSchedule);
  }
}
