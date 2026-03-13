package com.sabin.service;

import com.sabin.model.Note;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service  // Registers as Spring bean in IOC container
public class NoteService {
    private List<Note> notes = new ArrayList<>();
    private Long nextId = 1L;

    // Get all notes
    public List<Note> getAllNotes() {
        return new ArrayList<>(notes);  // Defensive copy
    }

    // Get note by ID
    public Optional<Note> getNoteById(Long id) {
        return notes.stream().filter(n -> n.getId().equals(id)).findFirst();
    }

    // Create new note
    public Note createNote(Note note) {
        note.setId(nextId++);
        notes.add(note);
        return note;
    }

    // Update note
    public Optional<Note> updateNote(Long id, Note updatedNote) {
        return getNoteById(id).map(note -> {
            note.setTitle(updatedNote.getTitle());
            note.setContent(updatedNote.getContent());
            return note;
        });
    }

    // Delete note
    public boolean deleteNote(Long id) {
        return notes.removeIf(n -> n.getId().equals(id));
    }
}

