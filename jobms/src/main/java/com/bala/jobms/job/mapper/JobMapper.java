package com.bala.jobms.job.mapper;

import com.bala.jobms.job.DTO.JobDTO;
import com.bala.jobms.job.Job;
import com.bala.jobms.job.external.Company;
import com.bala.jobms.job.external.Review;

import java.util.List;

public class JobMapper {
    public static JobDTO mapJob(Job job, Company company, List<Review> review){
        JobDTO jobDTO = new JobDTO();
        jobDTO.setId(job.getId());
        jobDTO.setTitle(job.getTitle());
        jobDTO.setDescription(job.getDescription());
        jobDTO.setLocation(job.getLocation());
        jobDTO.setMaxSalary(job.getMaxSalary());
        jobDTO.setMinSalary(job.getMinSalary());
        jobDTO.setCompany(company);
        jobDTO.setReview(review);

        return  jobDTO;
    }
}
