package com.bala.jobms.job.impl;


import com.bala.jobms.job.AppConfig;
import com.bala.jobms.job.DTO.JobDTO;
import com.bala.jobms.job.Job;
import com.bala.jobms.job.JobRepository;
import com.bala.jobms.job.JobService;
import com.bala.jobms.job.clients.CompanyClient;
import com.bala.jobms.job.clients.ReviewClient;
import com.bala.jobms.job.external.Company;
import com.bala.jobms.job.external.Review;
import com.bala.jobms.job.mapper.JobMapper;
import com.netflix.discovery.converters.Auto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobServiceImpl implements JobService {
    // private List<Job> jobs = new ArrayList<>();
    JobRepository jobRepository;
    @Autowired
    AppConfig appConfig;
    @Autowired
    CompanyClient companyClient;
    @Autowired
    ReviewClient reviewClient;

    public JobServiceImpl(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    @CircuitBreaker(name = "companyBreaker")
    public List<JobDTO> findAll() {

        List<Job> jobs=jobRepository.findAll();

        return jobs.stream().map(this::convertToDTO).collect(Collectors.toList());

    }

    public JobDTO convertToDTO(Job job){
        Company company= companyClient.getCompany(job.getCompanyId());

        List<Review> reviewResponse=reviewClient.getReviews(job.getCompanyId());
        return JobMapper.mapJob(job,company,reviewResponse);
    }

    @Override
    public void createJob(Job job) {
        jobRepository.save(job);
    }

    @Override
    public JobDTO getJobById(Long id) {
        Job job = jobRepository.findById(id).orElse(null);
        return convertToDTO(job);
    }

    @Override
    public boolean deleteJobById(Long id) {
        try {
            jobRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateJob(Long id, Job updatedJob) {
        Optional<Job> jobOptional = jobRepository.findById(id);
        if (jobOptional.isPresent()) {
            Job job = jobOptional.get();
            job.setTitle(updatedJob.getTitle());
            job.setDescription(updatedJob.getDescription());
            job.setMinSalary(updatedJob.getMinSalary());
            job.setMaxSalary(updatedJob.getMaxSalary());
            job.setLocation(updatedJob.getLocation());
            jobRepository.save(job);
            return true;
        }
        return false;
    }
}