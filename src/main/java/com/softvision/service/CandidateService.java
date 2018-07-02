package com.softvision.service;

import com.softvision.model.Candidate;

import java.util.List;

// TODO: Auto-generated Javadoc

/**
 * The Interface CandidateService.
 *
 * @author arun.p The Interface CandidateService.
 */
public interface CandidateService {

    /**
     * Adds the candidate.
     *
     * @param candidate the candidate
     * @return the candidate
     */
    Candidate addCandidate(final Candidate candidate);

    /**
     * Find candidate by id.
     *
     * @param id the id
     * @return the candidate
     */
    Candidate findCandidateById(final String id);

    /**
     * Delete candidate by id.
     *
     * @param id the id
     * @return the string
     */
    String deleteCandidateById(final String id);

    /**
     * Save all candidates.
     *
     * @param candidates the candidates
     * @return the list
     */
    List<Candidate> saveAllCandidates(final List<Candidate> candidates);

    /**
     * Find all candidates.
     *
     * @return the page
     */
    List<Candidate> findAllCandidates();

    /**
     * Update candidate.
     *
     * @param candidate the candidate
     * @param id        the id
     * @return the candidate
     */
    Candidate updateCandidate(final Candidate candidate, String id);

    /**
     * Delete all candidates.
     */
    void deleteAllCandidates();

    /**
     * Search candidate.
     *
     * @param searchAttribute the search attribute
     * @return the list
     */
    List<Candidate> searchCandidate(final String searchAttribute);

    /**
     * Find by is active is true.
     *
     * @param page1 the page
     * @return the page
     */
    List<Candidate> findByIsActiveIsTrue();

}
