package com.workify.jobms.job.mapper;

import java.util.List;

import com.workify.jobms.job.Job;
import com.workify.jobms.job.dto.JobDTO;
import com.workify.jobms.job.external.Company;
import com.workify.jobms.job.external.Review;

public class JobMapper {
    public static JobDTO mapToJobWithCompanyDto(
            Job job,
            Company company, List<Review> reviews) {

        JobDTO jobDTO = new JobDTO();
        jobDTO.setId(job.getId());
        jobDTO.setTitle(job.getTitle());
        jobDTO.setDescription(job.getDescription());
        jobDTO.setLocation(job.getLocation());
        jobDTO.setMaxSalary(job.getMaxSalary());
        jobDTO.setMinSalary(job.getMinSalary());
        jobDTO.setCompany(company);
        jobDTO.setReviews(reviews);

        return jobDTO;
    }
}
