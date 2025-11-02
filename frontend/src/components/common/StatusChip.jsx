import PropTypes from 'prop-types';
import { Chip } from '@mui/material';

const statusColor = {
  SUBMITTED: 'default',
  IN_REVIEW: 'info',
  REVISIONS_REQUESTED: 'warning',
  PUBLISHED: 'success',
  ACCEPTED: 'success',
  REJECTED: 'error',
  ARCHIVED: 'default',
};

const StatusChip = ({ status }) => (
  <Chip label={status.replace('_', ' ')} color={statusColor[status] || 'default'} size="small" />
);

StatusChip.propTypes = {
  status: PropTypes.string.isRequired,
};

export default StatusChip;
