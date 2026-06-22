package com.seniormonitor.server.service;

import com.seniormonitor.server.entity.ContactHistory;
import com.seniormonitor.server.repository.ContactHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ContactHistoryService {

    private final ContactHistoryRepository contactHistoryRepository;

    public ContactHistoryService(ContactHistoryRepository contactHistoryRepository) {
        this.contactHistoryRepository = contactHistoryRepository;
    }

    public List<ContactHistory> getHistory(Long seniorId) {
        return contactHistoryRepository.findBySeniorIdOrderByContactedAtDesc(seniorId);
    }
}
