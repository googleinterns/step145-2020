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

public class Plan {
  private final long id;
  private final String plan;
  private final String planName;
  private final String timestamp;

  public Plan(long id, String plan, String planName, String timestamp) {
    this.id = id;
    this.plan = plan;
    this.planName = planName;
    this.timestamp = timestamp;
  }
}
