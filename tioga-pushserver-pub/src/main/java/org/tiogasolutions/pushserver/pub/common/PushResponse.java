/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tiogasolutions.pushserver.pub.common;

import com.fasterxml.jackson.annotation.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

public class PushResponse implements Serializable {

  private final String domainId;
  private final String pushRequestId;

  private final LocalDateTime createdAt;
  private final RequestStatus requestStatus;

  private final List<String> notes = new ArrayList<>();

  @JsonCreator
  public PushResponse(@JsonProperty("domainId") String domainId,
                      @JsonProperty("pushRequestId") String pushRequestId,
                      @JsonProperty("createdAt") LocalDateTime createdAt,
                      @JsonProperty("requestStatus") RequestStatus requestStatus,
                      @JsonProperty("notes") Collection<String> notes) {

    this.domainId = domainId;
    this.pushRequestId = pushRequestId;

    this.createdAt = createdAt;
    this.requestStatus = requestStatus;

    this.notes.addAll(notes);
  }

  public String getDomainId() {
    return domainId;
  }

  public String getPushRequestId() {
    return pushRequestId;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public RequestStatus getRequestStatus() {
    return requestStatus;
  }

  public List<String> getNotes() {
    return notes;
  }
}
