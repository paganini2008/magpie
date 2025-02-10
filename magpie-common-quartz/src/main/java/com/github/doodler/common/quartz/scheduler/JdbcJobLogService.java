package com.github.doodler.common.quartz.scheduler;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.doodler.common.page.DefaultPageContent;
import com.github.doodler.common.page.PageContent;
import com.github.doodler.common.page.PageReader;
import com.github.doodler.common.page.PageRequest;
import com.github.doodler.common.page.PageResponse;

import lombok.RequiredArgsConstructor;

/**
 * @Description: JdbcJobLogService
 * @Author: Fred Feng
 * @Date: 24/08/2023
 * @Version 1.0.0
 */
@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
@RequiredArgsConstructor
public class JdbcJobLogService extends JobLogService implements InitializingBean {

    private static final String SQL_SELECT_COUNT_LOG = "select count(1) from qrtz_job_run_log where guid=:guid";
    private static final String SQL_UPDATE_LOG = "update qrtz_job_run_log set job_executor=:jobExecutor, executor_instance=:executorInstance, status=:status, end_time=:endTime, retry=:retry, errors=:errors where guid=:guid";
    private static final String SQL_INSERT_LOG = "insert into qrtz_job_run_log (id,job_group,job_name,trigger_name,trigger_group,description,class_name,url,method,headers,initial_parameter,guid,job_scheduler,scheduler_instance,start_time) values (:id,:jobGroup,:jobName,:triggerName,:triggerGroup,:description,:className,:url,:method,:headers,:initialParameter,:guid,:jobScheduler,:schedulerInstance,:startTime)";
    private static final String SQL_PAGE_COUNT_LOG = "select count(1) from (select * from qrtz_job_run_log where 1=1 %s) as T";
    private static final String SQL_PAGE_LOG = "select * from qrtz_job_run_log where 1=1 %s order by start_time desc limit %d offset %d";
    private static final String SQL_SUMMARY_LOG = "select status, count(*) as count from qrtz_job_run_log where retry=false group by status";

    private final DataSource dataSource;

    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    protected void writeLog(JobLog jobLog) {
        String guid = jobLog.getGuid();
        String selectSql = SQL_SELECT_COUNT_LOG;
        Long count = jdbcTemplate.queryForObject(selectSql, Collections.singletonMap("guid", guid),
                Long.class);
        if (count != null && count.longValue() > 0) {
            String updateSql = SQL_UPDATE_LOG;
            jdbcTemplate.update(updateSql, new BeanPropertySqlParameterSource(jobLog));
        } else {
            String insertSql = SQL_INSERT_LOG;
            jdbcTemplate.update(insertSql, new BeanPropertySqlParameterSource(jobLog));
        }
    }

    @Override
    public PageVo<JobLog> readLog(JobLogQuery logQuery) throws SQLException {
        JobLogPageReader pageReader = new JobLogPageReader(logQuery);
        PageResponse<JobLog> pageResponse = pageReader.list(PageRequest.of(logQuery.getPageNumber(),
                logQuery.getPageSize()));
        PageVo<JobLog> pageVo = new PageVo<JobLog>();
        pageVo.setContent(pageResponse.getContent().getContent());
        pageVo.setPageNumber(pageResponse.getPageNumber());
        pageVo.setPageSize(pageResponse.getPageSize());
        pageVo.setTotalPages(pageResponse.getTotalPages());
        pageVo.setTotalRecords(pageResponse.getTotalRecords());
        return pageVo;
    }

    @Override
    public List<Map<String, Object>> summarizeLog() {
        return jdbcTemplate.queryForList(SQL_SUMMARY_LOG, Collections.emptyMap());
    }

    /**
     * @Description: JobLogPageReader
     * @Author: Fred Feng
     * @Date: 23/10/2023
     * @Version 1.0.0
     */
    @RequiredArgsConstructor
    private class JobLogPageReader implements PageReader<JobLog> {

        private final JobLogQuery logQuery;

        @Override
        public long rowCount() throws SQLException {
            StringBuilder whereClause = new StringBuilder();
            if (StringUtils.isNotBlank(logQuery.getJobName())) {
                whereClause.append(" and job_name=:jobName");
            }
            if (StringUtils.isNotBlank(logQuery.getJobGroup())) {
                whereClause.append(" and job_group=:jobGroup");
            }
            if (StringUtils.isNotBlank(logQuery.getTriggerName())) {
                whereClause.append(" and trigger_name=:triggerName");
            }
            if (StringUtils.isNotBlank(logQuery.getTriggerGroup())) {
                whereClause.append(" and trigger_group=:triggerGroup");
            }
            if (logQuery.getStatus() >= 0) {
                whereClause.append(" and status=:status");
            }
            String selectCountSql = String.format(SQL_PAGE_COUNT_LOG, whereClause.toString());
            return jdbcTemplate.queryForObject(selectCountSql,
                    new BeanPropertySqlParameterSource(logQuery), Long.class);
        }

        @Override
        public PageContent<JobLog> list(int pageNumber, int offset, int limit, Object nextToken) throws SQLException {
            StringBuilder whereClause = new StringBuilder();
            if (StringUtils.isNotBlank(logQuery.getJobName())) {
                whereClause.append(" and job_name=:jobName");
            }
            if (StringUtils.isNotBlank(logQuery.getJobGroup())) {
                whereClause.append(" and job_group=:jobGroup");
            }
            if (StringUtils.isNotBlank(logQuery.getTriggerName())) {
                whereClause.append(" and trigger_name=:triggerName");
            }
            if (StringUtils.isNotBlank(logQuery.getTriggerGroup())) {
                whereClause.append(" and trigger_group=:triggerGroup");
            }
            if (logQuery.getStatus() >= 0) {
                whereClause.append(" and status=:status");
            }
            String selectSql = String.format(SQL_PAGE_LOG, whereClause.toString(), limit, offset);
            List<JobLog> jobLogs = jdbcTemplate.query(selectSql, new BeanPropertySqlParameterSource(logQuery),
                    new BeanPropertyRowMapper<JobLog>(JobLog.class));
            return new DefaultPageContent<>(jobLogs, null);
        }
    }
}
