import { useState } from 'react';
import PropTypes from 'prop-types';
import { Box, Button, Stack, TextField } from '@mui/material';

const buildInitialState = (initial) => ({
  title: initial?.title || '',
  abstractText: initial?.abstractText || '',
  keywords: initial?.keywords || '',
  content: initial?.content || '',
  changeLog: initial?.changeLog || '',
  filePath: initial?.filePath || '',
});

const PaperSubmissionForm = ({ initialValue, onSubmit, submitLabel }) => {
  const [form, setForm] = useState(buildInitialState(initialValue));

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    onSubmit(form);
  };

  return (
    <Box component="form" onSubmit={handleSubmit} noValidate>
      <Stack spacing={2}>
        <TextField
          label="Title"
          name="title"
          required
          value={form.title}
          onChange={handleChange}
        />
        <TextField
          label="Abstract"
          name="abstractText"
          required
          multiline
          minRows={3}
          value={form.abstractText}
          onChange={handleChange}
        />
        <TextField
          label="Keywords"
          name="keywords"
          value={form.keywords}
          onChange={handleChange}
          helperText="Comma-separated keywords"
        />
        <TextField
          label="Content"
          name="content"
          required
          multiline
          minRows={6}
          value={form.content}
          onChange={handleChange}
        />
        <TextField
          label="Change Log"
          name="changeLog"
          multiline
          minRows={3}
          value={form.changeLog}
          onChange={handleChange}
        />
        <TextField
          label="File Path / URL"
          name="filePath"
          value={form.filePath}
          onChange={handleChange}
        />
        <Button type="submit" variant="contained" color="primary">
          {submitLabel}
        </Button>
      </Stack>
    </Box>
  );
};

PaperSubmissionForm.propTypes = {
  initialValue: PropTypes.shape({
    title: PropTypes.string,
    abstractText: PropTypes.string,
    keywords: PropTypes.string,
    content: PropTypes.string,
    changeLog: PropTypes.string,
    filePath: PropTypes.string,
  }),
  onSubmit: PropTypes.func.isRequired,
  submitLabel: PropTypes.string,
};

PaperSubmissionForm.defaultProps = {
  initialValue: null,
  submitLabel: 'Submit Paper',
};

export default PaperSubmissionForm;
