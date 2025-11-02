import { useState } from 'react';
import PropTypes from 'prop-types';
import { Box, Button, MenuItem, Stack, TextField } from '@mui/material';

const recommendations = [
  { value: 'ACCEPT', label: 'Accept' },
  { value: 'MINOR_REVISION', label: 'Minor Revision' },
  { value: 'MAJOR_REVISION', label: 'Major Revision' },
  { value: 'REJECT', label: 'Reject' },
];

const ReviewForm = ({ onSubmit }) => {
  const [form, setForm] = useState({
    recommendation: 'ACCEPT',
    comments: '',
    plagiarismObservations: '',
  });

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    onSubmit(form);
  };

  return (
    <Box component="form" onSubmit={handleSubmit}>
      <Stack spacing={2}>
        <TextField
          select
          label="Recommendation"
          name="recommendation"
          value={form.recommendation}
          onChange={handleChange}
        >
          {recommendations.map((option) => (
            <MenuItem key={option.value} value={option.value}>
              {option.label}
            </MenuItem>
          ))}
        </TextField>
        <TextField
          label="Comments"
          name="comments"
          multiline
          minRows={4}
          required
          value={form.comments}
          onChange={handleChange}
        />
        <TextField
          label="Plagiarism Observations"
          name="plagiarismObservations"
          multiline
          minRows={3}
          value={form.plagiarismObservations}
          onChange={handleChange}
          helperText="Note any flagged sections you want to highlight."
        />
        <Button type="submit" variant="contained">
          Submit Review
        </Button>
      </Stack>
    </Box>
  );
};

ReviewForm.propTypes = {
  onSubmit: PropTypes.func.isRequired,
};

export default ReviewForm;
