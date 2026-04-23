import { useState, useEffect, useCallback } from 'react'
import './App.css'

const API = '/api'

/* ── Icons ── */
const PencilIcon = () => (
  <svg viewBox="0 0 24 24" fill="none" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" />
    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4Z" />
  </svg>
)

const TrashIcon = () => (
  <svg viewBox="0 0 24 24" fill="none" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <polyline points="3 6 5 6 21 6" />
    <path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6" />
    <path d="M10 11v6M14 11v6" />
    <path d="M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2" />
  </svg>
)

/* ── Helpers ── */
function generateValueOptions(valueMin, valueMax) {
  const range = valueMax - valueMin
  let step
  if (range <= 1) step = 0.1
  else if (range <= 20) step = 1
  else step = 10

  const opts = []
  let v = valueMin
  while (v <= valueMax + 0.0001) {
    opts.push(Math.round(v * 100) / 100)
    v += step
  }
  return opts
}

function buildPayload(form) {
  return {
    name: form.playerName.trim(),
    fullName: form.playerName.trim(),
    age: form.age.trim(),
    indexId: parseInt(form.indexId),
    playerIndexes: [
      { indexId: parseInt(form.indexId), value: parseFloat(form.value) }
    ]
  }
}

const initForm = { playerName: '', age: '', indexId: '', value: '' }

/* ════════════════════════════════
   Main Component
   ════════════════════════════════ */
export default function App() {
  const [players, setPlayers]       = useState([])
  const [indexers, setIndexers]     = useState([])
  const [form, setForm]             = useState(initForm)
  const [valueOpts, setValueOpts]   = useState([])
  const [editState, setEditState]   = useState(null) // { playerId, indexId }
  const [loading, setLoading]       = useState(false)
  const [submitting, setSubmitting] = useState(false)
  const [alert, setAlert]           = useState(null)  // { type: 'error'|'success', msg }

  /* ── Fetch helpers ── */
  const fetchPlayers = useCallback(async () => {
    setLoading(true)
    try {
      const res  = await fetch(`${API}/players`)
      const json = await res.json()
      setPlayers(json.data || [])
    } catch {
      showAlert('error', 'Không thể kết nối đến server. Hãy chắc chắn Spring Boot đang chạy.')
    } finally {
      setLoading(false)
    }
  }, [])

  const fetchIndexers = useCallback(async () => {
    try {
      const res  = await fetch(`${API}/indexers`)
      const json = await res.json()
      setIndexers(json.data || [])
    } catch { /* silent */ }
  }, [])

  useEffect(() => {
    fetchPlayers()
    fetchIndexers()
  }, [fetchPlayers, fetchIndexers])

  /* ── When indexId changes → recalculate value dropdown ── */
  useEffect(() => {
    if (!form.indexId || indexers.length === 0) {
      setValueOpts([])
      return
    }
    const idx = indexers.find(i => i.indexId === parseInt(form.indexId))
    if (idx) {
      setValueOpts(generateValueOptions(idx.valueMin, idx.valueMax))
    }
  }, [form.indexId, indexers])

  /* ── Flatten players → table rows ── */
  const flatRows = players
    .flatMap(p =>
      (p.playerIndexes || []).map(pi => ({
        rowKey:     `${p.playerId}-${pi.indexId}`,
        piId:       pi.id,
        playerId:   p.playerId,
        playerName: p.fullName,
        age:        p.age,
        indexId:    pi.indexId,
        indexName:  pi.indexName,
        value:      pi.value,
        allIndexes: p.playerIndexes  // keep for smart PUT
      }))
    )
    .sort((a, b) => a.piId - b.piId)

  /* ── Utils ── */
  function showAlert(type, msg) {
    setAlert({ type, msg })
    setTimeout(() => setAlert(null), 4000)
  }

  function handleFieldChange(e) {
    const { name, value } = e.target
    setForm(prev => ({
      ...prev,
      [name]: value,
      ...(name === 'indexId' ? { value: '' } : {})  // reset value when index changes
    }))
  }

  function resetForm() {
    setForm(initForm)
    setEditState(null)
    setValueOpts([])
  }

  /* ── Validation ── */
  function validate() {
    if (!form.playerName.trim()) return 'Vui lòng nhập tên người chơi.'
    if (!form.age.trim())        return 'Vui lòng nhập tuổi.'
    const age = parseInt(form.age)
    if (isNaN(age) || age <= 0 || age > 100) return 'Tuổi phải là số nguyên từ 1 đến 100.'
    if (!form.indexId)           return 'Vui lòng chọn Index name.'
    if (form.value === '')       return 'Vui lòng chọn Value.'
    return null
  }

  /* ── Submit: Add or Update ── */
  async function handleSubmit() {
    const err = validate()
    if (err) return showAlert('error', err)

    setSubmitting(true)
    try {
      if (editState) {
        /* Smart PUT: preserve all other indexes of this player */
        const player   = players.find(p => p.playerId === editState.playerId)
        const updatedIndexes = (player?.playerIndexes || []).map(pi =>
          pi.indexId === editState.indexId
            ? { indexId: parseInt(form.indexId), value: parseFloat(form.value) }
            : { indexId: pi.indexId, value: pi.value }
        )
        const payload = {
          name:         form.playerName.trim(),
          fullName:     form.playerName.trim(),
          age:          form.age.trim(),
          indexId:      parseInt(form.indexId),
          playerIndexes: updatedIndexes
        }
        const res  = await fetch(`${API}/players/${editState.playerId}`, {
          method:  'PUT',
          headers: { 'Content-Type': 'application/json' },
          body:    JSON.stringify(payload)
        })
        const json = await res.json()
        if (!json.success) throw new Error(json.message || 'Cập nhật thất bại')
        showAlert('success', 'Cập nhật người chơi thành công!')
      } else {
        /* POST new player */
        const res  = await fetch(`${API}/players`, {
          method:  'POST',
          headers: { 'Content-Type': 'application/json' },
          body:    JSON.stringify(buildPayload(form))
        })
        const json = await res.json()
        if (!json.success) {
          const errMsg = json.errors
            ? Object.values(json.errors).join(' | ')
            : json.message || 'Thêm thất bại'
          throw new Error(errMsg)
        }
        showAlert('success', 'Thêm người chơi thành công!')
      }

      resetForm()
      fetchPlayers()
    } catch (e) {
      showAlert('error', e.message)
    } finally {
      setSubmitting(false)
    }
  }

  /* ── Edit ── */
  function handleEdit(row) {
    setEditState({ playerId: row.playerId, indexId: row.indexId })
    setForm({
      playerName: row.playerName,
      age:        row.age,
      indexId:    String(row.indexId),
      value:      String(row.value)
    })
    // Scroll to form
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  /* ── Delete ── */
  async function handleDelete(playerId, playerName) {
    if (!window.confirm(`Xóa người chơi "${playerName}"?\nTất cả chỉ số của người chơi này cũng sẽ bị xóa.`)) return
    try {
      const res  = await fetch(`${API}/players/${playerId}`, { method: 'DELETE' })
      const json = await res.json()
      if (!json.success) throw new Error(json.message)
      showAlert('success', `Đã xóa người chơi "${playerName}".`)
      if (editState?.playerId === playerId) resetForm()
      fetchPlayers()
    } catch (e) {
      showAlert('error', e.message)
    }
  }

  /* ════════ Render ════════ */
  return (
    <div className="page-wrapper">
      <div className="card">

        {/* Title */}
        <h1 className="page-title">Player Information</h1>

        {/* Alert */}
        {alert && (
          <div className={`alert alert-${alert.type}`}>
            {alert.type === 'error' ? '⚠' : '✓'} {alert.msg}
          </div>
        )}

        {/* Edit-mode badge */}
        {editState && (
          <div className="edit-mode-badge">
            ✏ Đang chỉnh sửa người chơi (ID: {editState.playerId})
            <button className="btn-cancel-edit" onClick={resetForm} title="Hủy chỉnh sửa">✕</button>
          </div>
        )}

        {/* Form */}
        <div className="form-grid">
          <div className="form-group">
            <label htmlFor="playerName">Player name</label>
            <input
              id="playerName"
              name="playerName"
              placeholder="Player name"
              value={form.playerName}
              onChange={handleFieldChange}
              autoComplete="off"
            />
          </div>

          <div className="form-group">
            <label htmlFor="age">Player age</label>
            <input
              id="age"
              name="age"
              placeholder="Player age"
              value={form.age}
              onChange={handleFieldChange}
              autoComplete="off"
            />
          </div>

          <div className="form-group">
            <label htmlFor="indexId">Index name</label>
            <select id="indexId" name="indexId" value={form.indexId} onChange={handleFieldChange}>
              <option value="">Index name</option>
              {indexers.map(idx => (
                <option key={idx.indexId} value={idx.indexId}>
                  {idx.name}
                </option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label htmlFor="value">Value</label>
            <select
              id="value"
              name="value"
              value={form.value}
              onChange={handleFieldChange}
              disabled={!form.indexId}
            >
              <option value="">value</option>
              {valueOpts.map(v => (
                <option key={v} value={v}>{v}</option>
              ))}
            </select>
          </div>
        </div>

        {/* Add / Update Button */}
        <div className="btn-row">
          <button
            id="btn-submit"
            className="btn-add"
            onClick={handleSubmit}
            disabled={submitting}
          >
            {submitting ? '...' : editState ? 'Update' : 'Add'}
          </button>
        </div>

        {/* Table */}
        <div className="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>Id</th>
                <th>Player name</th>
                <th>Player age</th>
                <th>Index name</th>
                <th>Value</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr className="loading-row">
                  <td colSpan={6}>Đang tải dữ liệu...</td>
                </tr>
              ) : flatRows.length === 0 ? (
                <tr className="empty-row">
                  <td colSpan={6}>Chưa có dữ liệu. Hãy thêm người chơi đầu tiên!</td>
                </tr>
              ) : (
                flatRows.map((row, i) => (
                  <tr key={row.rowKey}>
                    <td>{i + 1}</td>
                    <td>{row.playerName}</td>
                    <td>{row.age}</td>
                    <td>{row.indexName}</td>
                    <td>{row.value}</td>
                    <td>
                      <div className="td-actions">
                        <button
                          id={`btn-edit-${row.piId}`}
                          className="btn-icon icon-edit"
                          title="Chỉnh sửa"
                          onClick={() => handleEdit(row)}
                        >
                          <PencilIcon />
                        </button>
                        <button
                          id={`btn-delete-${row.piId}`}
                          className="btn-icon icon-delete"
                          title="Xóa"
                          onClick={() => handleDelete(row.playerId, row.playerName)}
                        >
                          <TrashIcon />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

      </div>
    </div>
  )
}
