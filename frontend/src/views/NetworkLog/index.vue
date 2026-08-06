<template>
  <div class="network-log-container">
    <!-- 标题栏 -->
    <div class="page-header">
      <h2>📡 TCP/UDP 日志接收</h2>
      <div class="header-actions">
        <span class="action-btn" @click="showSendDialog">📤 发送测试</span>
        <el-tag :type="tcpRunning ? 'success' : 'danger'" size="small">
          TCP: {{ tcpRunning ? '9090' : '已停止' }}
        </el-tag>
        <el-tag :type="udpRunning ? 'success' : 'danger'" size="small">
          UDP: {{ udpRunning ? '9091' : '已停止' }}
        </el-tag>
        <el-tag type="info" size="small">
          已接收: {{ logs.length }}
        </el-tag>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon tcp">T</div>
        <div class="stat-info">
          <span class="stat-value">{{ tcpCount }}</span>
          <span class="stat-label">TCP</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon udp">U</div>
        <div class="stat-info">
          <span class="stat-value">{{ udpCount }}</span>
          <span class="stat-label">UDP</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon success">✓</div>
        <div class="stat-info">
          <span class="stat-value">{{ successCount }}</span>
          <span class="stat-label">成功</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon fail">✗</div>
        <div class="stat-info">
          <span class="stat-value">{{ failCount }}</span>
          <span class="stat-label">失败</span>
        </div>
      </div>
    </div>

    <!-- 日志面板 -->
    <div class="log-panel">
      <div class="panel-header">
        <span class="panel-title">📥 实时接收日志</span>
        <div class="panel-actions">
          <div class="filter-btns">
            <span
              v-for="item in filters"
              :key="item.value"
              class="filter-btn"
              :class="{ active: currentFilter === item.value }"
              @click="currentFilter = item.value"
            >{{ item.label }}</span>
          </div>
          <span class="clear-btn" @click="clearLogs">清空</span>
        </div>
      </div>
      <div class="log-list">
        <div v-if="filteredLogs.length === 0" class="empty-tip">
          <div class="empty-icon">📭</div>
          <div>暂无接收数据</div>
        </div>
        <div
          v-for="(log, index) in filteredLogs"
          :key="index"
          class="log-item"
          :class="[log.protocol.toLowerCase()]"
        >
          <div class="log-header">
            <span class="log-time">{{ formatTime(log.createTime) }}</span>
            <div class="log-tags">
              <span class="log-tag" :class="log.protocol.toLowerCase()">{{ log.protocol }}</span>
              <span class="log-tag" :class="log.direction.toLowerCase()">{{ log.direction === 'RECEIVE' ? '接收' : '发送' }}</span>
              <span v-if="log.clientIp" class="log-tag ip">{{ log.clientIp }}</span>
            </div>
          </div>
          <div class="log-content">{{ log.message }}</div>
          <div v-if="log.response" class="log-response">响应: {{ log.response }}</div>
        </div>
      </div>
    </div>

    <!-- 发送弹窗 -->
    <div v-if="sendDialogVisible" class="dialog-mask" @click.self="sendDialogVisible = false">
      <div class="send-dialog">
        <div class="dialog-header">
          <span>📤 发送测试数据</span>
          <span class="dialog-close" @click="sendDialogVisible = false">✕</span>
        </div>
        <div class="dialog-body">
          <!-- 协议选择 -->
          <div class="form-item">
            <label>协议类型</label>
            <div class="protocol-btns">
              <span
                class="proto-btn"
                :class="{ active: sendForm.protocol === 'TCP' }"
                @click="sendForm.protocol = 'TCP'"
              >TCP</span>
              <span
                class="proto-btn"
                :class="{ active: sendForm.protocol === 'UDP' }"
                @click="sendForm.protocol = 'UDP'"
              >UDP</span>
            </div>
          </div>
          <!-- 消息输入 -->
          <div class="form-item">
            <label>消息内容</label>
            <textarea
              v-model="sendForm.message"
              rows="3"
              placeholder="输入要发送的消息..."
            ></textarea>
          </div>
          <!-- 快捷按钮 -->
          <div class="form-item">
            <label>快捷发送</label>
            <div class="quick-btns">
              <span class="quick-btn" @click="quickSend('设备上线: 设备-001')">设备上线</span>
              <span class="quick-btn" @click="quickSend('传感器数据: 25.5℃')">传感器</span>
              <span class="quick-btn" @click="quickSend('告警: 温度过高')">告警</span>
              <span class="quick-btn" @click="quickSend('心跳包: ' + Date.now())">心跳</span>
              <span class="quick-btn" @click="quickSend('用户登录成功')">登录</span>
              <span class="quick-btn" @click="quickSend('订单创建完成')">订单</span>
            </div>
          </div>
          <!-- 发送按钮 -->
          <div class="dialog-footer">
            <span class="cancel-btn" @click="sendDialogVisible = false">取消</span>
            <span class="submit-btn" :class="{ disabled: sending }" @click="sendLog">
              {{ sending ? '发送中...' : '发送' }}
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator'
import SockJS from 'sockjs-client'
import Stomp from 'stompjs'
import axios from '@/api/base/http'

interface NetworkLog {
  protocol: string
  direction: string
  message: string
  response?: string
  clientIp?: string
  status: string
  errorMsg?: string
  createTime?: string
}

@Component({
  name: 'NetworkLog',
  meta: {
    pageIndex: 200,
    title: '日志收集',
    pageType: 'viceNav',
    belongTo: 'receive'
  }
})
export default class NetworkLog extends Vue {
  private logs: NetworkLog[] = []
  private currentFilter = 'ALL'
  private tcpRunning = true
  private udpRunning = true
  private stompClient: any = null

  // 发送弹窗
  private sendDialogVisible = false
  private sending = false
  private sendForm = {
    protocol: 'TCP',
    message: ''
  }

  private filters = [
    { label: '全部', value: 'ALL' },
    { label: 'TCP', value: 'TCP' },
    { label: 'UDP', value: 'UDP' }
  ]

  get filteredLogs(): NetworkLog[] {
    if (this.currentFilter === 'ALL') return this.logs
    return this.logs.filter(log => log.protocol === this.currentFilter)
  }

  get tcpCount(): number {
    return this.logs.filter(log => log.protocol === 'TCP').length
  }

  get udpCount(): number {
    return this.logs.filter(log => log.protocol === 'UDP').length
  }

  get successCount(): number {
    return this.logs.filter(log => log.status === 'SUCCESS').length
  }

  get failCount(): number {
    return this.logs.filter(log => log.status !== 'SUCCESS').length
  }

  created() {
    this.connectWebSocket()
    this.fetchLogs()
  }

  beforeDestroy() {
    this.disconnectWebSocket()
  }

  private formatTime(time?: string): string {
    if (!time) return ''
    return time.replace('T', ' ').substring(0, 19)
  }

  private connectWebSocket() {
    const socket = new SockJS('/ws')
    this.stompClient = Stomp.over(socket)
    this.stompClient.debug = null
    this.stompClient.connect({}, () => {
      this.stompClient.subscribe('/topic/network-logs', (message: any) => {
        const log: NetworkLog = JSON.parse(message.body)
        this.logs.unshift(log)
        if (this.logs.length > 500) this.logs.pop()
      })
    })
  }

  private disconnectWebSocket() {
    if (this.stompClient && this.stompClient.connected) {
      this.stompClient.disconnect()
    }
  }

  private async fetchLogs() {
    try {
      const res = await axios.get('/v1/network/logs')
      if (res.data.success) {
        this.logs = res.data.data || []
      }
    } catch (err) {
      console.error('Failed to fetch logs:', err)
    }
  }

  private showSendDialog() {
    this.sendForm.message = ''
    this.sendDialogVisible = true
  }

  private quickSend(msg: string) {
    this.sendForm.message = msg
  }

  private async sendLog() {
    if (!this.sendForm.message.trim() || this.sending) return

    this.sending = true
    try {
      const res = await axios.post('/v1/network/send', {
        protocol: this.sendForm.protocol,
        message: this.sendForm.message
      })
      if (res.data.success) {
        this.$message.success(`${this.sendForm.protocol} 发送成功`)
        this.sendForm.message = ''
      } else {
        this.$message.error(`发送失败: ${res.data.error}`)
      }
    } catch (err) {
      this.$message.error('网络错误')
    } finally {
      this.sending = false
    }
  }

  private async clearLogs() {
    try {
      await axios.delete('/v1/network/logs')
      this.logs = []
    } catch (err) {
      this.$message.error('清空失败')
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/theme.scss';

.network-log-container {
  width: 100%;
  height: 100%;
  background: $contBg;
  border-radius: 6px;
  padding: 5px;
  display: flex;
  flex-direction: column;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  padding: 10px 16px;
  background: $tableOddBgClr;
  border-radius: 4px;

  h2 {
    margin: 0;
    font-size: 16px;
    color: $normalWordsClr;
    font-weight: 600;
  }
}

.header-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.action-btn {
  padding: 5px 14px;
  background: $primaryBtnBg;
  color: #fff;
  border-radius: 4px;
  font-size: 13px;
  cursor: pointer;
  transition: opacity 0.2s;

  &:hover {
    opacity: 0.85;
  }
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
  margin-bottom: 10px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background: $tableOddBgClr;
  border-radius: 4px;
}

.stat-icon {
  width: 36px;
  height: 36px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: bold;
  color: #fff;
  margin-right: 10px;
}

.stat-icon.tcp { background: #409EFF; }
.stat-icon.udp { background: #E6A23C; }
.stat-icon.success { background: #67C23A; }
.stat-icon.fail { background: #F56C6C; }

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 18px;
  font-weight: 600;
  color: $normalWordsClr;
  line-height: 1.2;
}

.stat-label {
  font-size: 12px;
  color: $timeClr;
  margin-top: 2px;
}

.log-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: $tableOddBgClr;
  border-radius: 4px;
  overflow: hidden;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 16px;
  background: $centerTtlBgClr;
}

.panel-title {
  font-size: 14px;
  font-weight: 600;
  color: $normalWordsClr;
}

.panel-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.filter-btns {
  display: flex;
  gap: 4px;
}

.filter-btn {
  padding: 4px 12px;
  font-size: 12px;
  color: $timeClr;
  background: $inputBgClr;
  border-radius: 3px;
  cursor: pointer;

  &:hover { color: $normalWordsClr; }
  &.active { color: #fff; background: $primaryBtnBg; }
}

.clear-btn {
  font-size: 12px;
  color: $timeClr;
  cursor: pointer;
  &:hover { color: $normalWordsClr; }
}

.log-list {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
}

.log-list::-webkit-scrollbar { width: 6px; }
.log-list::-webkit-scrollbar-thumb { background: $centerTtlBgClr; border-radius: 3px; }

.empty-tip {
  text-align: center;
  padding: 60px 0;
  color: $timeClr;
}

.empty-icon { font-size: 40px; margin-bottom: 12px; }

.log-item {
  padding: 10px 14px;
  margin-bottom: 6px;
  background: $inputBgClr;
  border-radius: 4px;
  border-left: 3px solid #409EFF;
  &.udp { border-left-color: #E6A23C; }
}

.log-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.log-time {
  font-size: 12px;
  color: $timeClr;
  font-family: 'Consolas', monospace;
}

.log-tags { display: flex; gap: 6px; }

.log-tag {
  padding: 1px 6px;
  font-size: 11px;
  border-radius: 2px;
  &.tcp { color: #fff; background: #409EFF; }
  &.udp { color: #fff; background: #E6A23C; }
  &.receive { color: #fff; background: #909399; }
  &.send { color: #fff; background: #67C23A; }
  &.ip { color: $normalWordsClr; background: $centerTtlBgClr; }
}

.log-content {
  font-size: 13px;
  color: $normalWordsClr;
  word-break: break-all;
  line-height: 1.5;
}

.log-response {
  margin-top: 6px;
  padding: 4px 8px;
  background: $centerTtlBgClr;
  border-radius: 3px;
  font-size: 12px;
  color: $timeClr;
}

/* 发送弹窗 */
.dialog-mask {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.send-dialog {
  width: 480px;
  background: $dialogContBgClr;
  border-radius: 6px;
  overflow: hidden;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: $dialogTtlBgClr;
  color: $normalWordsClr;
  font-size: 14px;
  font-weight: 600;
}

.dialog-close {
  cursor: pointer;
  font-size: 16px;
  &:hover { opacity: 0.7; }
}

.dialog-body {
  padding: 16px;
}

.form-item {
  margin-bottom: 14px;

  label {
    display: block;
    margin-bottom: 6px;
    font-size: 13px;
    color: $timeClr;
  }

  textarea {
    width: 100%;
    padding: 8px 10px;
    background: $inputBgClr;
    border: 1px solid $centerTtlBgClr;
    border-radius: 4px;
    color: $normalWordsClr;
    font-size: 13px;
    resize: vertical;
    outline: none;
    box-sizing: border-box;
    font-family: inherit;

    &:focus { border-color: $primaryBtnBg; }
  }
}

.protocol-btns {
  display: flex;
  gap: 8px;
}

.proto-btn {
  flex: 1;
  padding: 8px;
  text-align: center;
  background: $inputBgClr;
  color: $timeClr;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.2s;

  &:hover { color: $normalWordsClr; }
  &.active { color: #fff; background: $primaryBtnBg; }
}

.quick-btns {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.quick-btn {
  padding: 4px 10px;
  background: $inputBgClr;
  color: $timeClr;
  border-radius: 3px;
  font-size: 12px;
  cursor: pointer;
  &:hover { color: $normalWordsClr; background: $centerTtlBgClr; }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid $centerTtlBgClr;
}

.cancel-btn {
  padding: 7px 18px;
  background: $inputBgClr;
  color: $timeClr;
  border-radius: 4px;
  font-size: 13px;
  cursor: pointer;
  &:hover { color: $normalWordsClr; }
}

.submit-btn {
  padding: 7px 24px;
  background: $primaryBtnBg;
  color: #fff;
  border-radius: 4px;
  font-size: 13px;
  cursor: pointer;
  &:hover { opacity: 0.85; }
  &.disabled { opacity: 0.5; cursor: not-allowed; }
}
</style>
