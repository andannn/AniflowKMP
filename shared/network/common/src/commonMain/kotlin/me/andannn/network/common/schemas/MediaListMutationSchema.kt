/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.network.common.schemas

const val MEDIA_LIST_MUTATION_SCHEMA = $$"""
mutation ($id: Int, $mediaId: Int, $progress: Int, $status: MediaListStatus, $score: Float, $progressVolumes: Int, $repeat: Int, $private: Boolean, $notes: String, $startedAt: FuzzyDateInput, $completedAt: FuzzyDateInput) {
  SaveMediaListEntry(id: $id, mediaId: $mediaId, progress: $progress, status: $status, score: $score, progressVolumes: $progressVolumes, repeat: $repeat, private: $private, notes: $notes, startedAt: $startedAt, completedAt: $completedAt) {
    id
    status
    score
    progress
    priority
    notes
    userId
    updatedAt
    progressVolumes
    repeat
    private
    notes
    startedAt {
      year
      month
      day
    }
    completedAt {
      year
      month
      day
    }
    media {
      id
    }
  }
}
"""
