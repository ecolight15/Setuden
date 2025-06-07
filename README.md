# Setuden

節電鯖（Energy-saving Server）固有の機能を実装したMinecraftプラグインです。

## 概要

Setudenは節電鯖専用に開発されたSpigotプラグインで、以下の機能を提供します：
- プレイヤー管理とWho is機能
- カスタムデス処理（プレイヤーゾンビシステム）
- コマンド保護とガード機能
- ワープシステム（BungeeCord対応）
- イベントスケジューラー
- スポーンエッグ使用制限
- カスタム権限システム

## 必要な環境

- **Minecraft Server**: Spigot/Paper 1.20.4以降
- **Java**: Java 8以降
- **必須プラグイン**:
  - EcoFramework (v0.29)
  - EcoGate (v0.3)
  - EcoAdmin
- **推奨プラグイン**:
  - EcoMQTTServerLog (v0.7)
  - LWC (v2.3.2-dev)

## インストール

1. 必須プラグインを先にインストールしてください
2. `Setuden-x.x.jar` を `plugins` フォルダに配置
3. サーバーを再起動
4. 設定ファイルが自動生成されます

## コマンド

### メインコマンド

#### `/setuden`
メインコマンド。サブコマンドが必要です。

- **権限**: `setuden`

#### `/setuden reload`
プラグインの設定をリロードします。

- **権限**: `setuden.reload`
- **使用例**: `/setuden reload`

#### `/setuden mkbase`
ベース作成コマンド。

- **権限**: `setuden.mkbase`
- **使用例**: `/setuden mkbase`

#### `/setuden mkbaseend`
エンドベース作成コマンド。

- **権限**: `setuden.mkbaseend`
- **使用例**: `/setuden mkbaseend`

### 権限管理コマンド

#### `/setuden permission set <player> <permission>`
プレイヤーに権限を付与します。

- **権限**: `setuden.permission.set`
- **使用例**: `/setuden permission set PlayerName some.permission`

#### `/setuden permission unset <player> <permission>`
プレイヤーから権限を削除します。

- **権限**: `setuden.permission.unset`
- **使用例**: `/setuden permission unset PlayerName some.permission`

### プレイヤー情報コマンド

#### `/whois <player>`
プレイヤーの詳細情報を表示します。オートコンプリート機能付き。

- **権限**: `setuden.whois`
- **使用例**: `/whois PlayerName`

### メッセージ設定

#### `/setmsg`
メッセージ設定コマンド。

- **権限**: `setuden.setmsg`

### エントランス機能

#### `/ent` / `/entrance`
エントランス機能を使用します。

- **権限**: `setuden.entrance`

#### `/setent` / `/setentrance`
エントランスを設定します。

- **権限**: `setuden.setentrance`

### プレイヤーゾンビシステム

#### `/plzombie <true|false|status>`
プレイヤーゾンビ機能の有効/無効を切り替えます。

- **権限**: `setuden.plzombie`
- **使用例**: 
  - `/plzombie true` - 機能を有効化
  - `/plzombie false` - 機能を無効化
  - `/plzombie status` - 現在の状態を確認

### テストコマンド

#### `/test`
テスト用コマンド。

- **権限**: `setuden.test`

## 主要機能

### 1. プレイヤーゾンビシステム

プレイヤーが死亡した際に、その場所にプレイヤーの装備を持ったゾンビを生成する機能です。

**特徴**:
- プレイヤーの装備品を引き継いだゾンビを生成
- 殺したプレイヤーをターゲットにする
- 高速移動能力付与
- カスタム名前表示

### 2. スポーンエッグ保護

OP権限を持たないプレイヤーによるスポーンエッグの使用を制限します。

**保護対象**:
- 右クリックでの使用
- エンティティへの右クリック使用
- メインハンド・オフハンド両方を監視

### 3. コマンド保護システム

危険なコマンドの実行を制限し、使用を記録します。

**保護対象のコマンド**:
- `/pl`, `/plugin` - プラグイン一覧表示
- `/reload`, `/stop` - サーバー制御
- `/fill` - ブロック一括配置
- `/op`, `/deop` - OP権限管理
- 各種管理系コマンド

### 4. カスタムダメージシステム

#### 奈落落下保護
- Y座標-100以下でのダメージを軽減
- Y座標0に自動テレポート

#### 死亡回避システム
- メインワールドでの致命的ダメージを軽減
- 体力を回復して死亡を回避

### 5. ワープシステム

BungeeCordと連携したワープ機能を提供します。

**機能**:
- 保留中のテレポートの管理
- プラグインメッセージングによる跨サーバー通信
- プレイヤー接続時の自動テレポート

### 6. イベントスケジューラー

時刻ベースのイベント実行システムです。

**イベントタイプ**:
- メッセージ送信
- プレイヤーキック
- サーバーシャットダウン
- セーブ実行
- シグナル送信

## 設定ファイル

### config.yml

```yaml
# 最新リソース番号
newestresource: 1

# エンドワールドの接頭辞
end_world_prefix: end

# クリスタルガード対象ワールド接頭辞
crystal_guard_world_prefix: end

# サーバーオープン間隔（分）
sever-open-interval: 40

# スケジューラー機能の有効/無効
setuden-scheduler: false

# メインワールド名
level-name: world

# フロストガード対象ワールド
guard-frost:
  - world
  - world_nether  
  - world_thr_end

# テスト設定
test:
    aaa: str1
    bbb: str2
    ccc: str3
```

### permissions.yml

カスタム権限設定ファイル。プレイヤーごとの権限管理に使用されます。

## データベース機能

### WhoisDB

プレイヤー情報を管理するデータベース機能を提供します。

**格納情報**:
- プレイヤー接続履歴
- プレイヤー詳細情報
- 検索・オートコンプリート機能用データ

## 開発情報

### プロジェクト構造

```
src/main/java/jp/minecraftuser/setuden/
├── Setuden.java           # メインクラス
├── command/              # コマンド実装
├── config/               # 設定管理
├── db/                   # データベース関連
├── listener/             # イベントリスナー
├── managers/             # 各種マネージャー
├── scheduler/            # スケジューラー機能
├── tasks/                # 非同期タスク
├── timer/                # タイマー処理
└── trie/                 # Trie データ構造
```

### ビルド方法

```bash
mvn clean compile
mvn package
```

## ライセンス

本プラグインはGNU Lesser General Public License v3.0の下で公開されています。
詳細は [LICENSE](LICENSE) ファイルを参照してください。

## 注意

- このプラグインは節電鯖専用に開発されており、他の環境での動作は保証されません
- 設定や機能の転用についてはライセンスに従ってください
- 本プラグインの使用は自己責任でお願いします

## 作者

@ecolight
