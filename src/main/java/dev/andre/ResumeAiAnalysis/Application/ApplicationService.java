package dev.andre.ResumeAiAnalysis.Application;

import lombok.Getter;
import org.springframework.stereotype.Service;

@Getter
@Service
public class ApplicationService {

    private ApplicationRepository applicationRepository;

    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    public ApplicationEntity save(ApplicationEntity archive){
        return applicationRepository.save(archive);
    }



}
