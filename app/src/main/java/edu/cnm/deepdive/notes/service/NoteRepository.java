package edu.cnm.deepdive.notes.service;

import edu.cnm.deepdive.notes.model.dao.NoteDao;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class NoteRepository {

  private final NoteDao noteDao;

  @Inject
  NoteRepository(NoteDao noteDao) {
    this.noteDao = noteDao;
  }
}
