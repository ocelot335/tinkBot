package edu.java.services;

import edu.java.clients.dto.LinkUpdate;

public interface IMessageTransporter {
    void send(LinkUpdate update);
}
